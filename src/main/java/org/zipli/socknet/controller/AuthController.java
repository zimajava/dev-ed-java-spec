package org.zipli.socknet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
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
    @PostMapping("/signup")
    public ResponseEntity<?> addUser(@Valid @RequestBody SignupRequest signupRequest) {
        //method realization
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        //method realization
        return ResponseEntity.ok("Here will be JwtResponse");
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailConfirmationService emailConfirmationService;

    @PostMapping(value = "/emailConfirmation")
    public ResponseEntity<?> sendConfirmationEmail(User user) {

        User existingUser = userRepository.getUserByEmail(user.getEmail());
        if (existingUser != null) {
            return ResponseEntity
                    .badRequest()
                    .body("This email already exists!");
        } else {
            userRepository.save(user);
            UserDetailsImpl userDetailsImpl = new UserDetailsImpl(user);
            String token = jwtUtils.generateJwtToken(userDetailsImpl);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("Complete Registration!");
            mailMessage.setFrom("zipli.socknet@gmail.com");
            mailMessage.setText("To confirm your account, please click here : "
                    + "http://localhost:8080/confirm-account?token=" + token);//поменять ссылку после деплоя

            emailConfirmationService.sendEmail(mailMessage);
        }

        return ResponseEntity.ok("User registered successfully!");
    }
}
