package org.zipli.socknet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.zipli.socknet.models.User;
import org.zipli.socknet.repositories.modelsRepositories.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.security.services.UserDetailsImpl;


@Service
public class EmailConfirmationService {

    @Autowired
    private JavaMailSender javaMailSender;
    private UserDetailsImpl userDetailsImpl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailConfirmationService emailConfirmationService;

    @Async
    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }

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
