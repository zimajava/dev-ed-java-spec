package org.zipli.socknet.service.getUsers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.zipli.socknet.dto.response.UserResponse;
import org.zipli.socknet.exception.chat.GetAllUsersException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.service.chat.GetUsersService;
import org.zipli.socknet.service.email.EmailConfirmationService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class GetUsersServiceTest {
    private final String id = "ddjfdlkfje";
    private final String email = "ddjfdlkfje@gmail.com";
    private final String password = "Password5";
    private final String userName = "Valve";
    private final String nickName = "Valve";
    private final byte[] avatar = new byte[1];

    @Autowired
    GetUsersService getUsersService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    EmailConfirmationService emailConfirmationService;

    UserResponse userResponse = new UserResponse(id, userName, avatar);
    User user = new User(email, password, userName, nickName);
    List<UserResponse> usersResponse = new ArrayList<>();
    List<User> users = new ArrayList<>();

    @BeforeEach
    void setUp() {
        user.setId(id);
        user.setAvatar(avatar);
        users.add(user);
        usersResponse.add(userResponse);
    }

    @Test
    void getAllUsersTest_Pass() {
        Mockito.when(userRepository.findAllByIsConfirm(true)).thenReturn(users);
        assertEquals(getUsersService.getAllUsers().toString(), usersResponse.toString());
    }

    @Test
    void getAllUsersTest_Null() {
        Mockito.when(userRepository.findAllByIsConfirm(true)).thenReturn(null);
        assertThrows(GetAllUsersException.class, () -> {
            getUsersService.getAllUsers();
        });
    }
}


