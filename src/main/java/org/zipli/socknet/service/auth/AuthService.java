package org.zipli.socknet.service.auth;

import org.springframework.stereotype.Service;
import org.zipli.socknet.exception.AuthException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.service.email.EmailConfirmationService;

@Service
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final EmailConfirmationService emailConfirmationService;

    public AuthService(UserRepository userRepository, JwtUtils jwtUtils, EmailConfirmationService emailConfirmationService) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.emailConfirmationService = emailConfirmationService;
    }

    @Override
    public User login(String emailOrUsername, String password) {
        User result;
        if (emailOrUsername.contains("@")) {
            result = userRepository.findUserByEmailAndPassword(emailOrUsername, password);
        } else {
            result = userRepository.findUserByUserNameAndPassword(emailOrUsername, password);
        }
        if (result == null) {
            throw new AuthException("User does not exist!");
        } else if (!result.isConfirm()) {
            throw new AuthException("User does not pass email confirmation!");
        } else {
            return result;
        }
    }

    @Override
    public void registration(User user) {

    }
}
