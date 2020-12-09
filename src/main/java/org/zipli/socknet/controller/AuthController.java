package org.zipli.socknet.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zipli.socknet.model.User;
import org.springframework.web.bind.annotation.*;
import org.zipli.socknet.payload.request.LoginRequest;
import org.zipli.socknet.payload.request.SignupRequest;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.security.services.UserDetailsImpl;
import org.zipli.socknet.services.EmailConfirmationService;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/zipli/auth", consumes = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final EmailConfirmationService emailConfirmationService;

    public AuthController(UserRepository userRepository, JwtUtils jwtUtils, EmailConfirmationService emailConfirmationService) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.emailConfirmationService = emailConfirmationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> addUser(@Valid @RequestBody SignupRequest signupRequest) {

        User existingUser = userRepository.getUserByEmail(signupRequest.getEmail());
        if (existingUser != null) {
            return ResponseEntity
                    .badRequest()
                    .body("This email already exists!");
        } else {
            User user = new User(signupRequest.getEmail(),
                    signupRequest.getPassword(),
                    signupRequest.getUserName(),
                    signupRequest.getNickName());
            userRepository.save(user);
            UserDetails userDetails = new UserDetailsImpl(user);
            String token = jwtUtils.generateJwtToken(userDetails);

            emailConfirmationService.sendEmail(signupRequest, token);
        }

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/confirm-account")
    public ResponseEntity<?> emailConfirm(@Valid @RequestBody @RequestParam("token") String token, SignupRequest signupRequest, LoginRequest loginRequest) {
        if (token != null) {
            User user = userRepository.getUserByEmail(signupRequest.getEmail());
            user.isConfirm();
            userRepository.save(user);
            authenticateUser(loginRequest);
            return ResponseEntity.ok("Account verified");

        } else {
            return ResponseEntity
                    .badRequest()
                    .body("Error. The link is invalid or broken!");
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        //method realization
        return ResponseEntity.ok("Here will be JwtResponse");
    }
}
