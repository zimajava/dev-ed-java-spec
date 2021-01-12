package org.zipli.socknet.service.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.response.LoginResponse;
import org.zipli.socknet.exception.auth.AuthException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.security.services.UserDetailsImpl;
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
    public LoginResponse login(String emailOrUsername, String password) {
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
            String token = jwtUtils.generateJwtToken(new UserDetailsImpl(result), result.getEmail());
            return new LoginResponse(result.getId(), token, token);
        }
    }

    @Override
    public void registration(User user) {
        User existingUser = userRepository.getUserByEmail(user.getEmail());
        if (existingUser != null) {
            throw new AuthException("This email already exists!");
        } else {
            userRepository.save(user);
            UserDetails userDetails = new UserDetailsImpl(user);
            String token = jwtUtils.generateJwtToken(userDetails, user.getEmail());
            emailConfirmationService.sendEmail(user.getEmail(), token);
        }
    }

}
