package org.zipli.socknet.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zipli.socknet.exception.AuthException;
import org.zipli.socknet.exception.NotConfirmAccountException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.payload.request.LoginRequest;
import org.zipli.socknet.payload.request.SignupRequest;
import org.zipli.socknet.service.auth.AuthService;
import org.zipli.socknet.service.email.EmailConfirmationService;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/zipli/auth", consumes = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final EmailConfirmationService emailConfirmationService;
    private final AuthService authService;

    public AuthController(EmailConfirmationService emailConfirmationService, AuthService authService) {
        this.emailConfirmationService = emailConfirmationService;
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

    @PostMapping("/confirm-account")
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

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        //method realization
        return ResponseEntity.ok("Here will be JwtResponse");
    }
}
