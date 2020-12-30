package org.zipli.socknet.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.zipli.socknet.exception.NotConfirmAccountException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
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
    public void sendEmail(String email, String token) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
        try {
            helper.setTo(email);
            helper.setSubject("Complete Registration!");
            helper.setFrom("zipli.socknet@gmail.com");

            String htmlMsg = "<h3>Confirm your mail</h3>"
                    + "<p style=\"font-size:18px;\">To confirm your account, please click "
                    + "<strong>"
                    + "<a href=\"" + deploy + "/confirm-mail?token=" + token + "\" target=\"_blank\">here</a>"
                    + "</strong>"
                    + "</p>"
                    + "<br/>"
                    + "<br/>"
                    + "<br/>"
                    + "<img src='http://www.apache.org/images/asf_logo_wide.gif'>";

            message.setContent(htmlMsg, "text/html");
            new Thread(() -> {
                javaMailSender.send(message);
            }).start();
        } catch (MessagingException e) {
            log.error("Your description here", e);
        }
    }

    public String confirmAccount(String token) {

        if (token != null) {
            String userName = jwtUtils.getUserNameFromJwtToken(token);
            User user = userRepository.getByUserName(userName);
            user.setConfirm(true);
            userRepository.save(user);
            return "Account verified";
        } else {
            throw new NotConfirmAccountException("Error. The token is invalid or broken!");
        }
    }
}
