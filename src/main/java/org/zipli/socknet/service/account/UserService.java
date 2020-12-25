package org.zipli.socknet.service.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.User;
import org.zipli.socknet.payload.request.MyAccountChange;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.security.services.UserDetailsImpl;
import org.zipli.socknet.service.email.EmailConfirmationService;

import javax.mail.MessagingException;


@Service
public class UserService implements IUserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailConfirmationService emailConfirmationService;

    @Autowired
    private JwtUtils jwtUtils;


    @Override
    @Transactional
    public User findUser(String userId) throws GetUserExeption {
        if (userId == null) {
            throw new GetUserExeption("not correct id");
        }
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new GetUserExeption("not correct id");
        }
        return user;
    }


    @Override
    @Transactional
    public User updateAvatar(MyAccountChange data) throws UpdateAvatarException {
        if (data.getUserId() == null || data.getAvatar() == null) {
            throw new UpdateAvatarException("data is null");
        }
        User user = userRepository.getUserById(data.getUserId());
        if (user == null) {
            throw new UpdateAvatarException("not correct id");
        }
        try {
            user.setAvatar(data.getAvatar());
            userRepository.save(user);
            return user;
        } catch (UpdateAvatarException e) {
            throw new UpdateAvatarException("not correct avatar");
        }
    }

    @Override
    public User updateNickName(MyAccountChange data) throws UpdateNickNameException {
        if (data.getUserId() == null || data.getNickName() == null) {
            throw new UpdateNickNameException("data is null");
        }
        User user = userRepository.getUserById(data.getUserId());
        if (user == null) {
            throw new UpdateNickNameException("not correct id");
        }
        try {
            user.setNickName(data.getNickName());
            userRepository.save(user);
            return user;
        } catch (UpdateNickNameException e) {
            throw new UpdateNickNameException("not correct nickName");
        }
    }


    @Override
    public User updateEmail(MyAccountChange data) throws UpdateEmailException {
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
        try {
            user.setEmail(data.getEmail());
            user.setConfirm(false);
            userRepository.save(user);
            UserDetails userDetails = new UserDetailsImpl(user);
            String token = jwtUtils.generateJwtToken(userDetails);
            emailConfirmationService.sendEmail(data.getEmail(), token);
            return user;
        } catch (UpdateEmailException | MessagingException e) {
            throw new UpdateEmailException("email already in use");
        }
    }


    @Override
    public User updatePassword(MyAccountChange data) throws UpdatePasswordExсeption {
        if (data.getUserId() == null || data.getPassword() == null) {
            throw new UpdatePasswordExсeption("data is null");
        }
        User user = userRepository.getUserById(data.getUserId());
        if (user == null) {
            throw new UpdatePasswordExсeption("not correct id");
        }
        try {
            user.setPassword(data.getPassword());
            userRepository.save(user);
            return user;
        } catch (UpdatePasswordExсeption e) {
            throw new UpdatePasswordExсeption("not correct password");
        }
    }
}
