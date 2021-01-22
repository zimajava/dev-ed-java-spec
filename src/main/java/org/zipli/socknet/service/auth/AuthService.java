package org.zipli.socknet.service.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.response.LoginResponse;
import org.zipli.socknet.exception.ErrorStatusCode;
import org.zipli.socknet.exception.auth.AuthException;
import org.zipli.socknet.repository.model.User;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.security.services.UserDetailsImpl;
import org.zipli.socknet.service.user.EmailConfirmationService;

@Service
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final EmailConfirmationService emailConfirmationService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtils jwtUtils, EmailConfirmationService emailConfirmationService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.emailConfirmationService = emailConfirmationService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse login(String emailOrUsername, String password) {
        User result;
        if (emailOrUsername.contains("@")) {
            result = userRepository.getUserByEmail(emailOrUsername);
        } else {
            result = userRepository.getUserByUserName(emailOrUsername);
        }
        if (result == null) {
            throw new AuthException(ErrorStatusCode.USER_DOES_NOT_EXIST);
        } else {
            if (!passwordEncoder.matches(password, result.getPassword())) {
                throw new AuthException(ErrorStatusCode.PASSWORD_INCORRECT);
            } else if (!result.isConfirm()) {
                throw new AuthException(ErrorStatusCode.USER_DOES_NOT_PASS_EMAIL_CONFIRM);
            } else {
                String token = jwtUtils.generateJwtToken(new UserDetailsImpl(result), result.getEmail());
                return new LoginResponse(result.getId(), token, token);
            }
        }
    }

    @Override
    public void registration(User user) {
        User existingUser = userRepository.getUserByEmail(user.getEmail());
        if (existingUser != null) {
            throw new AuthException(ErrorStatusCode.EMAIL_ALREADY_EXISTS);
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            UserDetails userDetails = new UserDetailsImpl(user);
            String token = jwtUtils.generateJwtToken(userDetails, user.getEmail());
            emailConfirmationService.sendEmail(user.getEmail(), token);
        }
    }
}
