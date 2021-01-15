package org.zipli.socknet.service.getUsers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.zipli.socknet.exception.ErrorStatusCode;
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
    private final String email = "email@gmail.com";
    private final String userName = "Valve";
    private final String nickName = "Nicki";
    private final String password = "qwerty";

    @Autowired
    GetUsersService getUsersService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    EmailConfirmationService emailConfirmationService;

    User user = new User(email, password, userName, nickName);
    List<User> users = new ArrayList<>();

    @BeforeEach
    void setUp() {
        user.setId("ddjfdlkfje");
        users.add(user);
    }

    @Test
    void getAllUsersTest_Pass() {
        Mockito.when(userRepository.findAll()).thenReturn(users);
        assertEquals(getUsersService.getAllUsers(), users);
    }

    @Test
    void getAllUsersTest_Null() {
        Mockito.when(userRepository.findAll()).thenReturn(null);
        assertThrows(GetAllUsersException.class, () -> {
            getUsersService.getAllUsers();
        });
    }
}


