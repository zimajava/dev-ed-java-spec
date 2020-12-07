package org.zipli.socknet.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zipli.socknet.models.User;
import org.zipli.socknet.payload.request.LoginRequest;
import org.zipli.socknet.payload.request.SignupRequest;
import org.zipli.socknet.repositories.modelsRepositories.UserRepository;
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

    @Value("${deploy.app}")
    private String deploy;

    @PostMapping("/signup")
    public ResponseEntity<?> addUser(@Valid @RequestBody SignupRequest signupRequest) {

        User existingUser = userRepository.getUserByEmail(signupRequest.getEmail());
        if (existingUser != null) {
            return ResponseEntity
                    .badRequest()
                    .body("This email already exists!");
        } else {
            User user = new User(1,
                    signupRequest.getEmail(),
                    signupRequest.getPassword(),
                    signupRequest.getUserName(),
                    signupRequest.getNickName());
            userRepository.save(user);
            UserDetails userDetails = new UserDetailsImpl(user);
            String token = jwtUtils.generateJwtToken(userDetails);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(signupRequest.getEmail());
            mailMessage.setSubject("Complete Registration!");
            mailMessage.setFrom("zipli.socknet@gmail.com");
            mailMessage.setText("To confirm your account, please click here : "
                    + deploy + "/confirm-account?token=" + token);//поменять ссылку после деплоя

            emailConfirmationService.sendEmail(mailMessage);
        }

        return ResponseEntity.ok("User registered successfully!");
    }


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        //method realization
        return ResponseEntity.ok("Here will be JwtResponse");
    }
}
