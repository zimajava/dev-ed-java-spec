package org.zipli.socknet.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.zipli.socknet.dto.response.ErrorResponse;
import org.zipli.socknet.exception.ErrorStatusCode;
import org.zipli.socknet.exception.chat.GetAllUsersException;
import org.zipli.socknet.repository.model.User;
import org.zipli.socknet.service.user.GetUsersService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GetUsersControllerTest {

    private final String email = "email@gmail.com";
    private final String userName = "Valve";
    private final String nickName = "Nicki";
    private final String password = "qwerty";

    @Autowired
    GetUsersController getUsersController;

    @MockBean
    GetUsersService getUsersService;

    User user = new User(email, password, userName, nickName);
    List<User> users = new ArrayList<>();

    @BeforeEach
    void setUp() {
        user.setId("5ffc8765a11fd");
        users.add(user);
    }

    @Test
    void getAllUsersTest_Pass() {
        Mockito.doReturn(users)
                .when(getUsersService)
                .getAllUsers();
        assertEquals(ResponseEntity.ok(users),
                getUsersController.getAllUsers());
    }

    @Test
    void getAllUsersTest_Null() {
        GetAllUsersException e = new GetAllUsersException(ErrorStatusCode.USERS_DOES_NOT_EXIST);
        Mockito.doThrow(e)
                .when(getUsersService)
                .getAllUsers();
        assertEquals(ResponseEntity.badRequest().body(HttpStatus.BAD_REQUEST).getStatusCode(),
                getUsersController.getAllUsers().getStatusCode());
        assertEquals(new ErrorResponse(ErrorStatusCode.USERS_DOES_NOT_EXIST.getValue()),getUsersController.getAllUsers().getBody());
    }
}
