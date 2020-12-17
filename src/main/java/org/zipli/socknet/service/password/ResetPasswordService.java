package org.zipli.socknet.service.password;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.zipli.socknet.exception.InvalidTokenException;
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
    @Value("${deploy.app}")
    private String deploy;

    public ResetPasswordService(JavaMailSender javaMailSender, JwtUtils jwtUtils, UserRepository userRepository) {
        this.javaMailSender = javaMailSender;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
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
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>");
        javaMailSender.send(mailMessage);
    }

    public String generateResetPasswordToken(String email) {

        if (userRepository.existsByEmail(email)) {
            User user = userRepository.getUserByEmail(email);
            String userName = user.getUserName();
            UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(userRepository);
            return jwtUtils.generateJwtToken(userDetailsService.loadUserByUsername(userName));
        } else {
            throw new UserNotFoundException("Error. User is not founded.");
        }
    }

    public String resetPassword(String newPassword, String token) {

        if (token != null) {
            String userName = jwtUtils.getUserNameFromJwtToken(token);
            User user = userRepository.getByUserName(userName);
            String password = user.getPassword();
//            user = mongoTemplate.findOne(
//                    Query.query(Criteria.where("password").is(password)), User.class);
//            user.setPassword(newPassword);
//            mongoTemplate.save(user, "user");
            //тут нужно старый пароль поменять на новый

            // String query = ">db.mycol.update({'password':'" + password + "'},{$set:{'password':'" + newPassword + "'}})";
            // ^ Сменить название бд и коллекции
            return "Password successfully changed";
        } else {
            throw new InvalidTokenException("Error. Token is invalid or broken");
        }
    }
}
