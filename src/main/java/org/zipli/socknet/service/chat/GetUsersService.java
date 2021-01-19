package org.zipli.socknet.service.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zipli.socknet.dto.response.UserResponse;
import org.zipli.socknet.exception.ErrorStatusCode;
import org.zipli.socknet.exception.chat.GetAllUsersException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetUsersService {

    private final UserRepository userRepository;

    @Autowired
    public GetUsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    @Transactional
    public List<UserResponse> getAllUsers() throws GetAllUsersException {

        List<User> users = userRepository.findAllByIsConfirm(true);
        if (users == null) {
            throw new GetAllUsersException(ErrorStatusCode.USERS_DOES_NOT_EXIST);
        }
        return users.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }
}
