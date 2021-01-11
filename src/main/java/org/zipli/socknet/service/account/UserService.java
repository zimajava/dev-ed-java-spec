package org.zipli.socknet.service.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.User;
import org.zipli.socknet.payload.request.AvatarRequest;
import org.zipli.socknet.payload.request.EmailRequest;
import org.zipli.socknet.payload.request.NickNameRequest;
import org.zipli.socknet.payload.request.PasswordRequest;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.security.services.UserDetailsImpl;
import org.zipli.socknet.service.email.EmailConfirmationService;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final EmailConfirmationService emailConfirmationService;
    private final JwtUtils jwtUtils;


    @Autowired
    public UserService(UserRepository userRepository, EmailConfirmationService emailConfirmationService, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.emailConfirmationService = emailConfirmationService;
        this.jwtUtils = jwtUtils;
    }


    @Override
    @Transactional
    public User findUser(String userId) throws GetUserException {
        if (userId == null) {
            throw new GetUserException(ErrorStatusCode.USER_ID_NULL.getValue());
        }
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new GetUserException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT.getValue());
        }
        return user;
    }

    @Override
    public User deleteAvatar(String userId) throws DeleteAvatarException {
        if (userId == null) {
            throw new DeleteAvatarException(ErrorStatusCode.USER_ID_NULL.getValue());
        }
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new DeleteAvatarException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT.getValue());
        }
        user.setAvatar(null);
        userRepository.save(user);
        return user;
    }


    @Override
    @Transactional
    public User updateAvatar(AvatarRequest data) throws UpdateAvatarException {
        if (data.getUserId() == null || data.getAvatar() == null) {
            throw new UpdateAvatarException(ErrorStatusCode.DATA_IS_NULL.getValue());
        }
        User user = userRepository.getUserById(data.getUserId());
        if (user == null) {
            throw new UpdateAvatarException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT.getValue());
        }
        user.setAvatar(data.getAvatar());
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    public User updateNickName(NickNameRequest data) throws UpdateNickNameException {
        if (data.getUserId() == null || data.getNickName() == null) {
            throw new UpdateNickNameException(ErrorStatusCode.DATA_IS_NULL.getValue());
        }
        User user = userRepository.getUserById(data.getUserId());
        if (user == null) {
            throw new UpdateNickNameException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT.getValue());
        }
        user.setNickName(data.getNickName());
        userRepository.save(user);
        return user;
    }


    @Override
    @Transactional
    public User updateEmail(EmailRequest data) throws UpdateEmailException {
        if (data.getUserId() == null || data.getEmail() == null) {
            throw new UpdateEmailException(ErrorStatusCode.DATA_IS_NULL.getValue());
        }
        User user = userRepository.getUserById(data.getUserId());
        if (user == null) {
            throw new UpdateEmailException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT.getValue());
        }
        if (!(data.getEmail().contains("@"))) {
            throw new UpdateEmailException(ErrorStatusCode.EMAIL_DOES_NOT_CORRECT.getValue());
        }
        user.setEmail(data.getEmail());
        user.setConfirm(false);
        userRepository.save(user);
        UserDetails userDetails = new UserDetailsImpl(user);
        String token = jwtUtils.generateJwtToken(userDetails, data.getEmail());
        emailConfirmationService.sendEmail(data.getEmail(), token);
        return user;
    }


    @Override
    @Transactional
    public User updatePassword(PasswordRequest data) throws UpdatePasswordExсeption {
        if (data.getUserId() == null || data.getPassword() == null) {
            throw new UpdatePasswordExсeption(ErrorStatusCode.DATA_IS_NULL.getValue());
        }
        User user = userRepository.getUserById(data.getUserId());
        if (user == null) {
            throw new UpdatePasswordExсeption(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT.getValue());
        }
        user.setPassword(data.getPassword());
        userRepository.save(user);
        return user;
    }
}
