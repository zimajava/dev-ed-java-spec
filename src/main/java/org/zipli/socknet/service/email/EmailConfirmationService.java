package org.zipli.socknet.service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.zipli.socknet.exception.NotConfirmAccountException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.payload.request.SignupRequest;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;


@Service
public class EmailConfirmationService {

    private final JavaMailSender javaMailSender;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    @Value("${deploy.app}")
    private String deploy;

    public EmailConfirmationService(JavaMailSender javaMailSender, JwtUtils jwtUtils, UserRepository userRepository) {
        this.javaMailSender = javaMailSender;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    @Async
    public void sendEmail(String email, String token) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("zipli.socknet@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
                + deploy + "/zipli/auth/confirm-account?token=" + token);
        javaMailSender.send(mailMessage);
    }

    public String confirmAccount(String token) {

        if (token != null) {
            String userName = jwtUtils.getUserNameFromJwtToken(token);
            User user = userRepository.getByUserName(userName);
            user.isConfirm();
            userRepository.save(user);
            return "Account verified";
        } else {
            throw new NotConfirmAccountException("Error. The token is invalid or broken!");
        }
    }
}
