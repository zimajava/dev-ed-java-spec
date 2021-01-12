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
            throw new GetUserException("UserId is null");
        }
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new GetUserException("not correct id");
        }
        return user;
    }

    @Override
    @Transactional
    public User deleteAvatar(String userId) throws DeleteAvatarException {
        if (userId == null) {
            throw new DeleteAvatarException("UserId is null");
        }
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new DeleteAvatarException("not correct id");
        }
        user.setAvatar(null);
        userRepository.save(user);
        return user;
    }


    @Override
    @Transactional
    public User updateAvatar(AvatarRequest data) throws UpdateAvatarException {
        if (data.getUserId() == null || data.getAvatar() == null) {
            throw new UpdateAvatarException("data is null");
        }
        User user = userRepository.getUserById(data.getUserId());
        if (user == null) {
            throw new UpdateAvatarException("not correct id");
        }
        user.setAvatar(data.getAvatar());
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    public User updateNickName(NickNameRequest data) throws UpdateNickNameException {
        if (data.getUserId() == null || data.getNickName() == null) {
            throw new UpdateNickNameException("data is null");
        }
        User user = userRepository.getUserById(data.getUserId());
        if (user == null) {
            throw new UpdateNickNameException("not correct id");
        }
        user.setNickName(data.getNickName());
        userRepository.save(user);
        return user;
    }


    @Override
    @Transactional
    public User updateEmail(EmailRequest data) throws UpdateEmailException {
        if (data.getUserId() == null || data.getEmail() == null) {
            throw new UpdateEmailException("data is null");
        }
        User user = userRepository.getUserById(data.getUserId());
        if (user == null) {
            throw new UpdateEmailException("not correct id");
        }
        if (!(data.getEmail().contains("@"))) {
            throw new UpdateEmailException("not correct email");
        }
        User existingUser = userRepository.getUserByEmail(data.getEmail());
        if (existingUser != null) {
            throw new UpdateEmailException("This email already exists!");
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
            throw new UpdatePasswordExсeption("data is null");
        }
        User user = userRepository.getUserById(data.getUserId());
        if (user == null) {
            throw new UpdatePasswordExсeption("not correct id");
        }
        user.setPassword(data.getPassword());
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    public String deleteAccount(String userId) throws DeleteAccountException {
        if (userId == null) {
            throw new DeleteAccountException("UserId is null");
        }
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new DeleteAccountException("not correct id");
        }
        user.setEmail(null);
        user.setUserName(userId);
        user.setNickName("deleted account");
        user.setConfirm(false);
        user.setPassword(null);
        user.setAvatar(null);
        userRepository.save(user);
        return userId;
    }
}
