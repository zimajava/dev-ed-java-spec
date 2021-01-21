package org.zipli.socknet.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.zipli.socknet.dto.request.AvatarRequest;
import org.zipli.socknet.dto.request.EmailRequest;
import org.zipli.socknet.dto.request.NickNameRequest;
import org.zipli.socknet.dto.request.PasswordRequest;
import org.zipli.socknet.dto.response.FullUserInfo;
import org.zipli.socknet.exception.DeleteAccountException;
import org.zipli.socknet.exception.ErrorStatusCode;
import org.zipli.socknet.exception.GetUserException;
import org.zipli.socknet.exception.UpdatePasswordException;
import org.zipli.socknet.exception.account.DeleteAvatarException;
import org.zipli.socknet.exception.account.UpdateAvatarException;
import org.zipli.socknet.exception.account.UpdateEmailException;
import org.zipli.socknet.exception.account.UpdateNickNameException;
import org.zipli.socknet.repository.model.User;
import org.zipli.socknet.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AccountControllerTest {
    private final String email = "email@gmail.com";
    private final String userName = "Valve";
    private final String nickName = "Nicki";
    private final String password = "qwerty";

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

    User user = new User(email, password, userName, nickName);

    @BeforeEach
    void setUp() {
        user.setId("5ffc8765a11fd");
    }

    @Test
    void getUserTest_Pass() {
        Mockito.doReturn(user)
                .when(userService)
                .findUser(user.getId());

        assertEquals(ResponseEntity.ok(new FullUserInfo(userService.findUser(user.getId()))).getStatusCode(),
                accountController.getUser(user.getId()).getStatusCode());
    }

    @Test
    void getUserTest_NullId() {
        GetUserException e = new GetUserException(ErrorStatusCode.USER_ID_NULL);
        Mockito.doThrow(e)
                .when(userService)
                .findUser(null);
        assertEquals(ResponseEntity.badRequest().body(ErrorStatusCode.USER_ID_NULL.getValue()),
                accountController.getUser(null));
    }

    @Test
    void getUserTest_BadId() {
        GetUserException e = new GetUserException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT);
        Mockito.doThrow(e)
                .when(userService)
                .findUser(user.getId());
        assertEquals(ResponseEntity.badRequest().body(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT.getValue()),
                accountController.getUser(user.getId()));
    }

    @Test
    void deleteAvatarTest_Pass() {
        Mockito.doReturn(user)
                .when(userService)
                .deleteAvatar(user.getId());
        assertEquals(ResponseEntity.ok(new FullUserInfo(userService.deleteAvatar(user.getId()))).getStatusCode(),
                accountController.deleteAvatar(user.getId()).getStatusCode());
    }

    @Test
    void deleteAvatarTest_NullId() {
        DeleteAvatarException e = new DeleteAvatarException(ErrorStatusCode.USER_ID_NULL);
        Mockito.doThrow(e)
                .when(userService)
                .deleteAvatar(null);
        assertEquals(ResponseEntity.badRequest().body(e.getErrorStatusCode().getValue()),
                accountController.deleteAvatar(null));
    }

    @Test
    void deleteAvatarTest_BadId() {
        DeleteAvatarException e = new DeleteAvatarException(ErrorStatusCode.USER_ID_NULL);
        Mockito.doThrow(e)
                .when(userService)
                .deleteAvatar(user.getId());
        assertEquals(ResponseEntity.badRequest().body(e.getErrorStatusCode().getValue()),
                accountController.deleteAvatar(user.getId()));
    }

    @Test
    void updateAvatarTest_Pass() {
        Mockito.doReturn(user)
                .when(userService)
                .updateAvatar(avatarRequest);
        assertEquals(ResponseEntity.ok(new FullUserInfo(userService.updateAvatar(avatarRequest))).getStatusCode(),
                accountController.updateAvatar(avatarRequest).getStatusCode());
    }

    @Test
    void updateAvatarTest_NullData() {
        Mockito.doThrow(new UpdateAvatarException(ErrorStatusCode.DATA_IS_NULL))
                .when(userService)
                .updateAvatar(avatarRequest);
        assertEquals(ResponseEntity.badRequest().body(ErrorStatusCode.DATA_IS_NULL.getValue()),
                accountController.updateAvatar(avatarRequest));
    }

    @Test
    void updateAvatarTest_BadId() {
        UpdateAvatarException e = new UpdateAvatarException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT);
        Mockito.doThrow(e)
                .when(userService)
                .updateAvatar(avatarRequest);
        assertEquals(ResponseEntity.badRequest().body(e.getErrorStatusCode().getValue()),
                accountController.updateAvatar(avatarRequest));
    }

    @Test
    void updateNickNameTest_Pass() {
        Mockito.doReturn(user)
                .when(userService)
                .updateNickName(nickNameRequest);
        assertEquals(ResponseEntity.ok(new FullUserInfo(userService.updateNickName(nickNameRequest))).getStatusCode(),
                accountController.updateNickName(nickNameRequest).getStatusCode());
    }

    @Test
    void updateNickNameTest_NullData() {
        Mockito.doThrow(new UpdateNickNameException(ErrorStatusCode.DATA_IS_NULL))
                .when(userService)
                .updateNickName(nickNameRequest);
        assertEquals(ResponseEntity.badRequest().body((ErrorStatusCode.DATA_IS_NULL).getValue()),
                accountController.updateNickName(nickNameRequest));
    }

    @Test
    void updateNickNameTest_BadId() {
        UpdateNickNameException e = new UpdateNickNameException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT);
        Mockito.doThrow(e)
                .when(userService)
                .updateNickName(nickNameRequest);
        assertEquals(ResponseEntity.badRequest().body(e.getErrorStatusCode().getValue()),
                accountController.updateNickName(nickNameRequest));
    }

    @Test
    void updateEmailTest_Pass() {
        Mockito.doReturn(user)
                .when(userService)
                .updateEmail(emailRequest);
        assertEquals(ResponseEntity.ok(new FullUserInfo(userService.updateEmail(emailRequest))).getStatusCode(),
                accountController.updateEmail(emailRequest).getStatusCode());
    }

    @Test
    void updateEmailTest_NullData() {

        Mockito.doThrow(new UpdateEmailException(ErrorStatusCode.DATA_IS_NULL))
                .when(userService)
                .updateEmail(emailRequest);
        assertEquals(ResponseEntity.badRequest().body(ErrorStatusCode.DATA_IS_NULL.getValue()),
                accountController.updateEmail(emailRequest));
    }

    @Test
    void updateEmailTest_BadId() {
        UpdateEmailException e = new UpdateEmailException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT);
        Mockito.doThrow(e)
                .when(userService)
                .updateEmail(emailRequest);
        assertEquals(ResponseEntity.badRequest().body(e.getErrorStatusCode().getValue()),
                accountController.updateEmail(emailRequest));
    }

    @Test
    void updateEmailTest_DoubleEmail() {
        UpdateEmailException e = new UpdateEmailException(ErrorStatusCode.EMAIL_ALREADY_EXISTS);
        Mockito.doThrow(e)
                .when(userService)
                .updateEmail(emailRequest);
        assertEquals(ResponseEntity.badRequest().body(e.getErrorStatusCode().getValue()),
                accountController.updateEmail(emailRequest));
    }

    @Test
    void updateEmailTest_BadEmail() {
        UpdateEmailException e = new UpdateEmailException(ErrorStatusCode.EMAIL_DOES_NOT_CORRECT);
        Mockito.doThrow(e)
                .when(userService)
                .updateEmail(emailRequest);
        assertEquals(ResponseEntity.badRequest().body(e.getErrorStatusCode().getValue()),
                accountController.updateEmail(emailRequest));
    }

    @Test
    void updatePasswordTest_Pass() {
        Mockito.doReturn(user)
                .when(userService)
                .updatePassword(passwordRequest);
        assertEquals(accountController.updatePassword(passwordRequest).getStatusCode(),
                ResponseEntity.ok(new FullUserInfo(userService.updatePassword(passwordRequest))).getStatusCode());
    }

    @Test
    void updatePasswordTest_NullData() {
        Mockito.doThrow(new UpdatePasswordException(ErrorStatusCode.DATA_IS_NULL))
                .when(userService)
                .updatePassword(passwordRequest);

        assertEquals(ResponseEntity.badRequest().body(ErrorStatusCode.DATA_IS_NULL.getValue()),
                accountController.updatePassword(passwordRequest));
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
    void deleteAccountTest_Pass() {
        Mockito.doReturn(user.getId())
                .when(userService)
                .deleteAccount(user.getId());
        assertEquals(accountController.deleteAccount(user.getId()),
                ResponseEntity.ok(userService.deleteAccount(user.getId())));
    }

    @Test
    void deleteAccountTest_NullId() {
        DeleteAccountException e = new DeleteAccountException(ErrorStatusCode.USER_ID_NULL);
        Mockito.doThrow(e)
                .when(userService)
                .deleteAccount(null);
        assertEquals(ResponseEntity.badRequest()
                .body(ErrorStatusCode.USER_ID_NULL.getValue()), accountController.deleteAccount(null));
    }

    @Test
    void deleteAccountTest_BadId() {
        DeleteAccountException e = new DeleteAccountException(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT);
        Mockito.doThrow(e)
                .when(userService)
                .deleteAccount(user.getId());
        assertEquals(ResponseEntity.badRequest().body(ErrorStatusCode.USER_ID_DOES_NOT_CORRECT.getValue()),
                accountController.deleteAccount(user.getId()));
    }
}
