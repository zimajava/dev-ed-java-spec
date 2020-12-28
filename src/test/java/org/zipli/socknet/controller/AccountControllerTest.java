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
import org.zipli.socknet.service.account.UserService;


import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class AccountControllerTest {

    @Autowired
    AccountController accountController;

    @MockBean
    UserService userService;

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
    void getUserTest_NullId() {
        GetUserException e = new GetUserException("UserId is null");
        Mockito.doThrow(e)
                .when(userService)
                .findUser(null);
        assertEquals(accountController.getUser(null), ResponseEntity.badRequest().body(e));
    }

    @Test
    void getUserTest_BadId() {
        GetUserException e = new GetUserException("not correct id");
        Mockito.doThrow(e)
                .when(userService)
                .findUser("212");
        assertEquals(accountController.getUser("212"), ResponseEntity.badRequest().body(e));
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
    void updateAvatarTest_NullData() {
        UpdateAvatarException e = new UpdateAvatarException("data is null");
        Mockito.doThrow(e)
                .when(userService)
                .updateAvatar(null);
        assertEquals(accountController.updateAvatar(null), ResponseEntity.badRequest().body(e));
    }

    @Test
    void updateAvatarTest_BadId() {
        UpdateAvatarException e = new UpdateAvatarException("not correct id");
        Mockito.doThrow(e)
                .when(userService)
                .updateAvatar(myAccountChange);
        assertEquals(accountController.updateAvatar(myAccountChange), ResponseEntity.badRequest().body(e));
    }

    @Test
    void deleteAvatarTest_Pass() {
        Mockito.doReturn(user)
                .when(userService)
                .deleteAvatar("dssd");
        assertEquals(accountController.deleteAvatar("dssd"),
                ResponseEntity.ok(userService.deleteAvatar("dssd")));
    }

    @Test
    void deleteAvatarTest_NullId() {
        DeleteAvatarException e = new DeleteAvatarException("UserId is null");
        Mockito.doThrow(e)
                .when(userService)
                .deleteAvatar(null);
        assertEquals(accountController.deleteAvatar(null), ResponseEntity.badRequest().body(e));
    }

    @Test
    void deleteAvatarTest_BadId() {
        DeleteAvatarException e = new DeleteAvatarException("not correct id");
        Mockito.doThrow(e)
                .when(userService)
                .deleteAvatar("dssd");
        assertEquals(accountController.deleteAvatar("dssd"), ResponseEntity.badRequest().body(e));
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
    void updateNickNameTest_NullData() {
        UpdateNickNameException e = new UpdateNickNameException("data is null");
        Mockito.doThrow(e)
                .when(userService)
                .updateNickName(null);
        assertEquals(accountController.updateNickName(null), ResponseEntity.badRequest().body(e));
    }

    @Test
    void updateNickNameTest_BadId() {
        UpdateNickNameException e = new UpdateNickNameException("not correct id");
        Mockito.doThrow(e)
                .when(userService)
                .updateNickName(myAccountChange);
        assertEquals(accountController.updateNickName(myAccountChange), ResponseEntity.badRequest().body(e));
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
    void updateEmailTest_NullData()  {
        UpdateEmailException e = new UpdateEmailException("data is null");
        Mockito.doThrow(e)
                .when(userService)
                .updateEmail(null);
        assertEquals(accountController.updateEmail(null), ResponseEntity.badRequest().body(e));
    }

    @Test
    void updateEmailTest_BadId()  {
        UpdateEmailException e = new UpdateEmailException("not correct id");
        Mockito.doThrow(e)
                .when(userService)
                .updateEmail(myAccountChange);
        assertEquals(accountController.updateEmail(myAccountChange), ResponseEntity.badRequest().body(e));
    }

    @Test
    void updateEmailTest_BadEmail()  {
        UpdateEmailException e = new UpdateEmailException("not correct email");
        Mockito.doThrow(e)
                .when(userService)
                .updateEmail(myAccountChange);
        assertEquals(accountController.updateEmail(myAccountChange), ResponseEntity.badRequest().body(e));
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
    void updatePasswordTest_NullData() {
        UpdatePasswordExсeption e = new UpdatePasswordExсeption("data is null");
        Mockito.doThrow(e)
                .when(userService)
                .updatePassword(null);
        assertEquals(accountController.updatePassword(null), ResponseEntity.badRequest().body(e));
    }

    @Test
    void updatePasswordTest_BadId()  {
        UpdatePasswordExсeption e = new UpdatePasswordExсeption("not correct id");
        Mockito.doThrow(e)
                .when(userService)
                .updatePassword(myAccountChange);
        assertEquals(accountController.updatePassword(myAccountChange), ResponseEntity.badRequest().body(e));
    }
}
