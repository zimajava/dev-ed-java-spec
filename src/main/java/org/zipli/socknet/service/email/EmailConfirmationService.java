package org.zipli.socknet.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.zipli.socknet.exception.auth.NotConfirmAccountException;
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
    public void sendEmail(String email, String token) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
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
                    + "<img src='https://i.gifer.com/DfGU.gif'>";

            message.setContent(htmlMsg, "text/html");
            new Thread(() -> {
                try {
                    javaMailSender.send(message);
                } catch (Exception e) {
                    log.error("Error send message to email {} message {} class {}", email, e.getMessage(), e.getClass().getSimpleName());
                }

            }).start();
        } catch (MessagingException e) {
            log.error("Error to create message to email {} message {} class {}", email, e.getMessage(), e.getClass().getSimpleName());
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
