package org.zipli.socknet.service.password;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zipli.socknet.exception.UserNotFoundException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.security.services.UserDetailsServiceImpl;

@Component
public class ResetPasswordService {

    private final JavaMailSender javaMailSender;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Value("${deploy.app}")
    private String deploy;

    public ResetPasswordService(JavaMailSender javaMailSender, JwtUtils jwtUtils, UserRepository userRepository, User user, UserDetailsServiceImpl userDetailsService) {
        this.javaMailSender = javaMailSender;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    public void sendEmailForChangingPassword(String email) {

        String token = generateResetPasswordToken(email);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Here's the link to reset your password");
        mailMessage.setFrom("zipli.socknet@gmail.com");
        mailMessage.setText("<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + deploy + "/zipli/auth/reset_password?token=" + token + "<p>Change my password</p>"
                + "<br>" + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>");
        javaMailSender.send(mailMessage);
    }

    public String generateResetPasswordToken(String email) {

        User user = userRepository.getUserByEmail(email);
        if (user != null) {
            String userName = user.getUserName();
            return jwtUtils.generateJwtToken(userDetailsService.loadUserByUsername(userName));
        } else {
            throw new UserNotFoundException("Error. User is not founded.");
        }
    }

    @Transactional
    public String resetPassword(String newPassword, String token) {

            String userName = jwtUtils.getUserNameFromJwtToken(token);
            User user = userRepository.getByUserName(userName);
            String password = user.getPassword();
            if (!password.isEmpty()) {
                user.setPassword(newPassword);
            }
            userRepository.save(user);
            return "Password successfully changed";
    }
}
