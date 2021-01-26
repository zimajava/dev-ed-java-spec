package org.zipli.socknet.service.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zipli.socknet.exception.ErrorStatusCode;
import org.zipli.socknet.exception.auth.UserNotFoundException;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.repository.model.User;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.security.services.UserDetailsServiceImpl;

@Component
public class ResetPasswordService {

    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Value("${deploy.app}")
    private String deploy;

    public ResetPasswordService(JavaMailSender javaMailSender, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, UserRepository userRepository, UserDetailsServiceImpl userDetailsService) {
        this.javaMailSender = javaMailSender;
        this.passwordEncoder = passwordEncoder;
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
        if (email == null) {
            throw new UserNotFoundException(ErrorStatusCode.EMAIL_DOES_NOT_CORRECT);
        } else {
            User user = userRepository.getUserByEmail(email);
            if (user != null) {
                String userName = user.getUserName();
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                String token = jwtUtils.generateJwtToken(userDetails, email);
                return token;
            } else {
                throw new UserNotFoundException(ErrorStatusCode.USER_DOES_NOT_EXIST);
            }
        }
    }

    @Transactional
    public String resetPassword(String token, String newPassword) {

        if (token == null) {
            throw new UserNotFoundException(ErrorStatusCode.USER_DOES_NOT_EXIST);
        } else if (newPassword != null) {

            String userName = jwtUtils.getUserNameFromJwtToken(token);
            String codedPassword = passwordEncoder.encode(newPassword);
            userRepository.updatePasswordInUsersModel(userName, codedPassword);

            return "Password successfully changed";
        } else {
            throw new UserNotFoundException(ErrorStatusCode.PASSWORD_IS_NULL);
        }
    }
}
