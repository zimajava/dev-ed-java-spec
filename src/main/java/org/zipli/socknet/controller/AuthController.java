package org.zipli.socknet.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zipli.socknet.exception.AuthException;
import org.zipli.socknet.exception.InvalidTokenException;
import org.zipli.socknet.exception.NotConfirmAccountException;
import org.zipli.socknet.exception.UserNotFoundException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.payload.request.LoginRequest;
import org.zipli.socknet.payload.request.SignupRequest;
import org.zipli.socknet.service.auth.AuthService;
import org.zipli.socknet.service.email.EmailConfirmationService;
import org.zipli.socknet.service.password.ResetPasswordService;

import javax.validation.Valid;

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
        if (signupRequest.getEmail() == null
                || signupRequest.getNickName() == null
                || signupRequest.getPassword() == null
                || signupRequest.getUserName() == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Not valid values");
        } else {
            User user = new User(signupRequest.getEmail(),
                    signupRequest.getPassword(),
                    signupRequest.getUserName(),
                    signupRequest.getNickName());
            try {
                authService.registration(user);
            } catch (AuthException e) {
                return ResponseEntity
                        .badRequest()
                        .body(e);
            }
            return ResponseEntity.ok("User registered successfully!");
        }
    }

    @GetMapping("/confirm-account")
    public ResponseEntity<?> emailConfirm(@Valid @RequestParam("token") String token) {
        try {
            emailConfirmationService.confirmAccount(token);
        } catch (NotConfirmAccountException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e);
        }
        return ResponseEntity.ok("Account verified");
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<?> processForgotPassword(@Valid @RequestParam("email") String email) {
        try {
            resetPasswordService.generateResetPasswordToken(email);
        } catch (UserNotFoundException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e);
        }
        resetPasswordService.sendEmailForChangingPassword(email);
        return ResponseEntity.ok("Password can be changed");
    }

    @PostMapping("/reset_password")
    public ResponseEntity<?> processResetPassword(@Valid @RequestParam("token") String token, String newPassword) {
        try {
            resetPasswordService.resetPassword(token, newPassword);
        } catch (InvalidTokenException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e);
        }
        return ResponseEntity.ok("Password successfully changed");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        //method realization
        return ResponseEntity.ok("Here will be JwtResponse");
    }
}
