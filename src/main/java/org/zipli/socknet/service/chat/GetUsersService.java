package org.zipli.socknet.service.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zipli.socknet.exception.ErrorStatusCode;
import org.zipli.socknet.exception.chat.GetAllUsersException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetUsersService {

    private final UserRepository userRepository;

    @Autowired
    public GetUsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    @Transactional
    public List<User> getAllUsers() throws GetAllUsersException {

        List<User> users = userRepository.findAll();
        if (users == null) {
            throw new GetAllUsersException(ErrorStatusCode.USERS_DOES_NOT_EXIST);
        }
        List<User> finalUsers = new ArrayList<>();
        for (User user : users) {
            if (user.getPassword() != null) {
                finalUsers.add(user);
            }
        }
        return finalUsers;
    }
}
