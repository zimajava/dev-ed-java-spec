package org.zipli.socknet.controller;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.User;
import org.zipli.socknet.payload.request.MyAccountChange;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.service.account.UserService;
import org.zipli.socknet.service.email.EmailConfirmationService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AccountControllerTest {

    @Autowired
    AccountController accountController;

    @MockBean
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    EmailConfirmationService emailConfirmationService;

    @MockBean
    MyAccountChange myAccountChange;

    @MockBean
    User user;

    @Test
    void getUserTest_Pass() {

        Mockito.doReturn(user)
                .when(userService)
                .findUser("ddjfdlkfje");

        assertEquals(accountController.getUser("ddjfdlkfje"),
                ResponseEntity.ok(userService.findUser("ddjfdlkfje")));
    }

    @Test
    void getUserTest_Fail() {
        Mockito.doThrow(GetUserExeption.class)
                .when(userService)
                .findUser(null);

        assertNotEquals(accountController.getUser(null), ResponseEntity.ok(userService.findUser("ddjfdlkfje")));
    }


    @Test
    void updateAvatarTest_Pass() {

        Mockito.doReturn(user)
                .when(userService)
                .updateAvatar(myAccountChange);
        assertEquals(accountController.updateAvatar(myAccountChange),
                ResponseEntity.ok(userService.updateAvatar(myAccountChange)));
    }

    @Test
    void updateAvatarTest_Fail() {
        Mockito.doThrow(UpdateAvatarException.class)
                .when(userService)
                .updateAvatar(null);
        assertNotEquals(accountController.updateAvatar(null),
                ResponseEntity.ok(userService.updateAvatar(myAccountChange)));
    }


    @Test
    void updateNickNameTest_Pass() {

        Mockito.doReturn(user)
                .when(userService)
                .updateNickName(myAccountChange);
        assertEquals(accountController.updateNickName(myAccountChange),
                ResponseEntity.ok(userService.updateNickName(myAccountChange)));
    }

    @Test
    void updateNickNameTest_Fail() {
        Mockito.doThrow(UpdateAvatarException.class)
                .when(userService)
                .updateNickName(null);
        assertNotEquals(accountController.updateNickName(null),
                ResponseEntity.ok(userService.updateNickName(myAccountChange)));
    }


    @Test
    void updateEmailTest_Pass() {

        Mockito.doReturn(user)
                .when(userService)
                .updateEmail(myAccountChange);
        assertEquals(accountController.updateEmail(myAccountChange),
                ResponseEntity.ok(userService.updateEmail(myAccountChange)));
    }

    @Test
    void updateEmailTest_Fail() {
        Mockito.doThrow(UpdateEmailException.class)
                .when(userService)
                .updateEmail(null);
        assertNotEquals(accountController.updateEmail(null),
                ResponseEntity.ok(userService.updateEmail(myAccountChange)));
    }

    @Test
    void updatePasswordTest_Pass() {

        Mockito.doReturn(user)
                .when(userService)
                .updatePassword(myAccountChange);
        assertEquals(accountController.updatePassword(myAccountChange),
                ResponseEntity.ok(userService.updatePassword(myAccountChange)));
    }

    @Test
    void updatePasswordTest_Fail() {
        Mockito.doThrow(UpdatePasswordExсeption.class)
                .when(userService)
                .updatePassword(null);
        assertNotEquals(accountController.updatePassword(null),
                ResponseEntity.ok(userService.updatePassword(myAccountChange)));
    }
}
