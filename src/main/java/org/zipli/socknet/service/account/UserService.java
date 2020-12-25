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
        if (data.getUserId() == null  || data.getAvatar()==null) {
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
    public User updateNickName(MyAccountChange data) throws UpdateNickNameExeption {
        if (data.getUserId() == null || data.getNickName()==null) {
            throw new UpdateNickNameExeption("data is null");
        }
        User user = userRepository.getUserById(data.getUserId());
        if (user == null) {
            throw new UpdateNickNameExeption("not correct id");
        }
        try {
            user.setNickName(data.getNickName());
            userRepository.save(user);
            return user;
        } catch (UpdateNickNameExeption e) {
            throw new UpdateNickNameExeption("not correct nickName");
        }
    }


    @Override
    public User updateEmail(MyAccountChange data) throws UpdateEmailExeption {
        if (data.getUserId() == null || data.getEmail()==null) {
            throw new UpdateEmailExeption("data is null");
        }
        User user = userRepository.getUserById(data.getUserId());
        if (user == null) {
            throw new UpdateEmailExeption("not correct id");
        }
        if (!(data.getEmail().contains("@"))) {
            throw new UpdateEmailExeption("not correct email");
        }
        try {
            user.setEmail(data.getEmail());
            user.setConfirm(false);
            userRepository.save(user);
            UserDetails userDetails = new UserDetailsImpl(user);
            String token = jwtUtils.generateJwtToken(userDetails);
            emailConfirmationService.sendEmail(data.getEmail(), token);
            return user;
        } catch (UpdateEmailExeption | MessagingException e) {
            throw new UpdateEmailExeption("email already in use");
        }
    }


    @Override
    public User updatePassword(MyAccountChange data) throws UpdatePasswordExeption {
        if (data.getUserId() == null || data.getPassword()==null) {
            throw new UpdatePasswordExeption("data is null");
        }
        User user = userRepository.getUserById(data.getUserId());
        if (user == null) {
            throw new UpdatePasswordExeption("not correct id");
        }
        try {
            user.setPassword(data.getPassword());
            userRepository.save(user);
            return user;
        } catch (UpdatePasswordExeption e) {
            throw new UpdatePasswordExeption("not correct password");
        }
    }
}
