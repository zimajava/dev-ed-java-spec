package org.zipli.socknet.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.zipli.socknet.payload.request.SignupRequest;


@Service
public class EmailConfirmationService {

    private final JavaMailSender javaMailSender;

    public EmailConfirmationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Value("${deploy.app}")
    private String deploy;

    @Async
    public void sendEmail(SignupRequest signupRequest, String token) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(signupRequest.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("zipli.socknet@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
                + deploy + "/confirm-account?token=" + token);
        javaMailSender.send(mailMessage);
    }
}
