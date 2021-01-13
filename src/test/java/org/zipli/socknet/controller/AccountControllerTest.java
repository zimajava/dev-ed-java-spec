package org.zipli.socknet.controller;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.exception.account.*;
import org.zipli.socknet.model.User;
import org.zipli.socknet.payload.request.AvatarRequest;
import org.zipli.socknet.payload.request.EmailRequest;
import org.zipli.socknet.payload.request.NickNameRequest;
import org.zipli.socknet.payload.request.PasswordRequest;
import org.zipli.socknet.service.account.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AccountControllerTest {

    @Autowired
    AccountController accountController;

    @MockBean
    UserService userService;

    @MockBean
    AvatarRequest avatarRequest;

    @MockBean
    EmailRequest emailRequest;

    @MockBean
    NickNameRequest nickNameRequest;

    @MockBean
    PasswordRequest passwordRequest;

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
        GetUserException e = new GetUserException(ErrorStatusCode.USER_ID_NULL);
        Mockito.doThrow(e)
                .when(userService)
                .findUser(null);
        assertEquals(ResponseEntity.badRequest()
                .body(ErrorStatusCode.USER_ID_NULL.getValue()),
                accountController.getUser(null));
    }

    @Test
    void getUserTest_BadId() {
        GetUserException e = new GetUserException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT);
        Mockito.doThrow(e)
                .when(userService)
                .findUser("212");
        assertEquals(ResponseEntity.badRequest()
                .body(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT.getValue()),
                accountController.getUser("212"));
    }

    @Test
    void deleteAvatarTest_Pass() {
        Mockito.doReturn(user)
                .when(userService)
                .deleteAvatar("dssd");
        assertEquals(ResponseEntity.ok(userService.deleteAvatar("dssd")), accountController.deleteAvatar("dssd"));
    }

    @Test
    void deleteAvatarTest_NullId() {
        DeleteAvatarException e = new DeleteAvatarException(ErrorStatusCode.USER_ID_NULL);
        Mockito.doThrow(e)
                .when(userService)
                .deleteAvatar(null);
        assertEquals(accountController.deleteAvatar(null), ResponseEntity.badRequest().body(e.getErrorStatusCode().getValue()));
    }

    @Test
    void deleteAvatarTest_BadId() {
        DeleteAvatarException e = new DeleteAvatarException(ErrorStatusCode.USER_ID_NULL);
        Mockito.doThrow(e)
                .when(userService)
                .deleteAvatar("dssd");
        assertEquals(accountController.deleteAvatar("dssd"), ResponseEntity.badRequest().body(e.getErrorStatusCode().getValue()));
    }

    @Test
    void updateAvatarTest_Pass() {
        Mockito.doReturn(user)
                .when(userService)
                .updateAvatar(avatarRequest);
        assertEquals(accountController.updateAvatar(avatarRequest),
                ResponseEntity.ok(userService.updateAvatar(avatarRequest)));
    }

    @Test
    void updateAvatarTest_NullData() {
        UpdateAvatarException e = new UpdateAvatarException(ErrorStatusCode.DATA_IS_NULL);
        Mockito.doThrow(e)
                .when(userService)
                .updateAvatar(null);
        assertEquals(accountController.updateAvatar(null), ResponseEntity.badRequest().body(e.getErrorStatusCode().getValue()));
    }

    @Test
    void updateAvatarTest_BadId() {
        UpdateAvatarException e = new UpdateAvatarException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT);
        Mockito.doThrow(e)
                .when(userService)
                .updateAvatar(avatarRequest);
        assertEquals(accountController.updateAvatar(avatarRequest), ResponseEntity.badRequest().body(e.getErrorStatusCode().getValue()));
    }

    @Test
    void updateNickNameTest_Pass() {
        Mockito.doReturn(user)
                .when(userService)
                .updateNickName(nickNameRequest);
        assertEquals(accountController.updateNickName(nickNameRequest),
                ResponseEntity.ok(userService.updateNickName(nickNameRequest)));
    }

    @Test
    void updateNickNameTest_NullData() {
        UpdateNickNameException e = new UpdateNickNameException(ErrorStatusCode.DATA_IS_NULL);
        Mockito.doThrow(e)
                .when(userService)
                .updateNickName(null);
        assertEquals(accountController.updateNickName(null), ResponseEntity.badRequest().body(e.getErrorStatusCode().getValue()));
    }

    @Test
    void updateNickNameTest_BadId() {
        UpdateNickNameException e = new UpdateNickNameException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT);
        Mockito.doThrow(e)
                .when(userService)
                .updateNickName(nickNameRequest);
        assertEquals(accountController.updateNickName(nickNameRequest), ResponseEntity.badRequest().body(e.getErrorStatusCode().getValue()));
    }

    @Test
    void updateEmailTest_Pass() {
        Mockito.doReturn(user)
                .when(userService)
                .updateEmail(emailRequest);
        assertEquals(accountController.updateEmail(emailRequest),
                ResponseEntity.ok(userService.updateEmail(emailRequest)));
    }

    @Test
    void updateEmailTest_NullData() {
        UpdateEmailException e = new UpdateEmailException(ErrorStatusCode.DATA_IS_NULL);
        Mockito.doThrow(e)
                .when(userService)
                .updateEmail(null);
        assertEquals(accountController.updateEmail(null), ResponseEntity.badRequest().body(e.getErrorStatusCode().getValue()));
    }

    @Test
    void updateEmailTest_BadId() {
        UpdateEmailException e = new UpdateEmailException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT);
        Mockito.doThrow(e)
                .when(userService)
                .updateEmail(emailRequest);
        assertEquals(accountController.updateEmail(emailRequest), ResponseEntity.badRequest().body(e.getErrorStatusCode().getValue()));
    }

    @Test
    void updateEmailTest_DoubleEmail() {
        UpdateEmailException e = new UpdateEmailException("This email already exists!");
        Mockito.doThrow(e)
                .when(userService)
                .updateEmail(emailRequest);
        assertEquals(accountController.updateEmail(emailRequest), ResponseEntity.badRequest().body(e));
    }

    @Test
    void updateEmailTest_BadEmail() {
        UpdateEmailException e = new UpdateEmailException(ErrorStatusCode.EMAIL_DOES_NOT_CORRECT);
        Mockito.doThrow(e)
                .when(userService)
                .updateEmail(emailRequest);
        assertEquals(accountController.updateEmail(emailRequest), ResponseEntity.badRequest().body(e.getErrorStatusCode().getValue()));
    }

    @Test
    void updatePasswordTest_Pass() {
        Mockito.doReturn(user)
                .when(userService)
                .updatePassword(passwordRequest);
        assertEquals(accountController.updatePassword(passwordRequest),
                ResponseEntity.ok(userService.updatePassword(passwordRequest)));
    }

    @Test
    void updatePasswordTest_NullData() {
        UpdatePasswordException e = new UpdatePasswordException(ErrorStatusCode.DATA_IS_NULL);
        Mockito.doThrow(e)
                .when(userService)
                .updatePassword(null);
        assertEquals(accountController.updatePassword(null), ResponseEntity.badRequest().body(e.getErrorStatusCode().getValue()));
    }

    @Test
    void updatePasswordTest_BadId() {
        UpdatePasswordException e = new UpdatePasswordException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT);
        Mockito.doThrow(e)
                .when(userService)
                .updatePassword(passwordRequest);
        assertEquals(accountController.updatePassword(passwordRequest), ResponseEntity.badRequest().body(e.getErrorStatusCode().getValue()));
    }

    @Test
    void deleteAccountTest_Pass()  {
        Mockito.doReturn(user.getId())
                .when(userService)
                .deleteAccount("ddjfdlkfje");
        assertEquals(accountController.deleteAccount("ddjfdlkfje"),
                ResponseEntity.ok(userService.deleteAccount("ddjfdlkfje")));
    }

    @Test
    void deleteAccountTest_NullId() {
        DeleteAccountException e = new DeleteAccountException("UserId is null");
        Mockito.doThrow(e)
                .when(userService)
                .deleteAccount(null);
        assertEquals(accountController.deleteAccount(null), ResponseEntity.badRequest().body(e));
    }

    @Test
    void deleteAccountTest_BadId() {
        DeleteAccountException e = new DeleteAccountException("not correct id");
        Mockito.doThrow(e)
                .when(userService)
                .deleteAccount("212");
        assertEquals(accountController.deleteAccount("212"), ResponseEntity.badRequest().body(e));
    }

}
