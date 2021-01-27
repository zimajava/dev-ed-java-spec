package org.zipli.socknet.service.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zipli.socknet.dto.request.AvatarRequest;
import org.zipli.socknet.dto.request.EmailRequest;
import org.zipli.socknet.dto.request.NickNameRequest;
import org.zipli.socknet.dto.request.PasswordRequest;
import org.zipli.socknet.exception.ErrorStatusCode;
import org.zipli.socknet.exception.account.*;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.repository.model.User;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.security.services.UserDetailsImpl;
import org.zipli.socknet.exception.SearchByParamsException;

import java.util.List;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final EmailConfirmationService emailConfirmationService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, EmailConfirmationService emailConfirmationService, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailConfirmationService = emailConfirmationService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User findUser(String userId) throws GetUserException {
        if (userId == null) {
            throw new GetUserException(ErrorStatusCode.USER_ID_NULL);
        }
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new GetUserException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT);
        }
        return user;
    }

    @Override
    @Transactional
    public User deleteAvatar(String userId) throws DeleteAvatarException {
        if (userId == null) {
            throw new DeleteAvatarException(ErrorStatusCode.USER_ID_NULL);
        }
        User user = userRepository.updateOrDeleteAvatar(userId, null);
        if (user == null) {
            throw new DeleteAvatarException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT);
        }
        return user;
    }

    @Override
    @Transactional
    public User updateAvatar(AvatarRequest data) throws UpdateAvatarException {
        if (data.getUserId() == null || data.getAvatar() == null) {
            throw new UpdateAvatarException(ErrorStatusCode.DATA_IS_NULL);
        }
        User user = userRepository.updateOrDeleteAvatar(data.getUserId(), data.getAvatar());
        if (user == null) {
            throw new UpdateAvatarException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT);
        }
        return user;
    }

    @Override
    @Transactional
    public User updateNickName(NickNameRequest data) throws UpdateNickNameException {
        if (data.getUserId() == null || data.getNickName() == null) {
            throw new UpdateNickNameException(ErrorStatusCode.DATA_IS_NULL);
        }
        User user = userRepository.updateNickName(data.getUserId(), data.getNickName());
        if (user == null) {
            throw new UpdateNickNameException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT);
        }
        return user;
    }

    @Override
    @Transactional
    public User updateEmail(EmailRequest data) throws UpdateEmailException {
        if (data.getUserId() == null || data.getEmail() == null) {
            throw new UpdateEmailException(ErrorStatusCode.DATA_IS_NULL);
        }
        if (!(data.getEmail().contains("@"))) {
            throw new UpdateEmailException(ErrorStatusCode.EMAIL_DOES_NOT_CORRECT);
        }
        User existingUser = userRepository.getUserByEmail(data.getEmail());
        if (existingUser != null) {
            throw new UpdateEmailException(ErrorStatusCode.EMAIL_ALREADY_EXISTS);
        }
        User user = userRepository.updateEmail(data.getUserId(), data.getEmail());
        if (user == null) {
            throw new UpdateEmailException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT);
        }
        UserDetails userDetails = new UserDetailsImpl(user);
        String token = jwtUtils.generateJwtToken(userDetails, data.getEmail());
        emailConfirmationService.sendEmail(data.getEmail(), token);
        return user;
    }

    @Override
    @Transactional
    public User updatePassword(PasswordRequest data) throws UpdatePasswordException {
        if (data.getUserId() == null || data.getPassword() == null) {
            throw new UpdatePasswordException(ErrorStatusCode.DATA_IS_NULL);
        }
        User user = userRepository.updatePassword(data.getUserId(), data.getPassword());
        if (user == null) {
            throw new UpdatePasswordException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT);
        }
        return user;
    }

    @Override
    @Transactional
    public String deleteAccount(String userId) throws DeleteAccountException {
        if (userId == null) {
            throw new DeleteAccountException(ErrorStatusCode.USER_ID_NULL);
        }
        userRepository.deleteById(userId);
        return "Account deleted(";
    }

    @Override
    @Transactional
    public List<User> getUsersBySearchParam(String param) throws SearchByParamsException {
        if (param == null) {
            throw new SearchByParamsException(ErrorStatusCode.PARAM_IS_NULL);
        }
        String searchParam = param.trim();

        if (searchParam.length() < 3) {
            throw new SearchByParamsException(ErrorStatusCode.PARAM_TOO_SHORT);
        }
        List<User> users = userRepository.findUsersBySearchParam(searchParam);
        if (users.isEmpty()) {
            throw new SearchByParamsException(ErrorStatusCode.USERS_DOES_NOT_EXIST_BY_PARAM);
        }
        return users;
    }
}
