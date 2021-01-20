package org.zipli.socknet.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zipli.socknet.dto.response.LoginResponse;
import org.zipli.socknet.exception.auth.AuthException;
import org.zipli.socknet.exception.auth.NotConfirmAccountException;
import org.zipli.socknet.exception.auth.UserNotFoundException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.payload.request.*;
import org.zipli.socknet.service.auth.AuthService;
import org.zipli.socknet.service.email.EmailConfirmationService;
import org.zipli.socknet.service.password.ResetPasswordService;

import javax.validation.Valid;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(value = "/zipli/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final EmailConfirmationService emailConfirmationService;
    private final ResetPasswordService resetPasswordService;
    private final AuthService authService;

    public AuthController(EmailConfirmationService emailConfirmationService, ResetPasswordService resetPasswordService, AuthService authService) {
        this.emailConfirmationService = emailConfirmationService;
        this.resetPasswordService = resetPasswordService;
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> addUser(@Valid @RequestBody SignupRequest signupRequest) {
        User user = new User(signupRequest.getEmail(),
                signupRequest.getPassword(),
                signupRequest.getUserName(),
                signupRequest.getNickName());
        try {
            authService.registration(user);
        } catch (AuthException e) {
            log.error("Failed registration by email {}, userName {}, nickName {}, reason {}", signupRequest.getEmail(),
                    signupRequest.getUserName(), signupRequest.getNickName(), e.getErrorStatusCode().getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getErrorStatusCode().getValue());
        }
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/confirm-mail")
    public ResponseEntity<?> emailConfirm(@Valid @RequestBody ConfirmMailRequest confirmMailRequest) {
        try {
            emailConfirmationService.confirmAccount(confirmMailRequest.getToken());
        } catch (NotConfirmAccountException e) {
            log.error("Failed confirm email tokenIsNull {}, reason {}", Objects.isNull(confirmMailRequest.getToken()), e.getErrorStatusCode().getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getErrorStatusCode().getValue());
        }
        return ResponseEntity.ok("Account verified");
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<?> processForgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            resetPasswordService.generateResetPasswordToken(forgotPasswordRequest.getEmail());
        } catch (UserNotFoundException e) {
            log.error("Failed restore password by email {}, reason {}", forgotPasswordRequest.getEmail(), e.getErrorStatusCode().getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getErrorStatusCode().getValue());
        }
        resetPasswordService.sendEmailForChangingPassword(forgotPasswordRequest.getEmail());
        return ResponseEntity.ok("Password can be changed");
    }

    @PostMapping("/reset_password")
    public ResponseEntity<?> processResetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            resetPasswordService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getPassword());
        } catch (UserNotFoundException e) {
            log.error("Failed update password tokenIsNull {}, reason {}",
                    Objects.isNull(resetPasswordRequest.getToken()), e.getErrorStatusCode().getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getErrorStatusCode().getValue());
        }
        return ResponseEntity.ok("Password successfully changed");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse;
        try {
            loginResponse = authService.login(loginRequest.getLogin(), loginRequest.getPassword());
        } catch (AuthException e) {
            log.error("Failed authenticate user by login {}, passwordIsNull {}, reason {}",
                    loginRequest.getLogin(), Objects.isNull(loginRequest.getPassword()), e.getErrorStatusCode().getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getErrorStatusCode().getValue());
        }
        return ResponseEntity.ok(loginResponse);
    }
}
