package org.zipli.socknet.service.account;

import com.sun.mail.iap.ByteArray;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.User;
import org.zipli.socknet.payload.request.MyAccountChange;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.service.email.EmailConfirmationService;



import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    User user;

    @MockBean
    EmailConfirmationService emailConfirmationService;

    @Test
    void getUserTest_Pass() {
        Mockito.when(userRepository.getUserById("ddjfdlkfje")).thenReturn(user);
        assertEquals(userService.findUser("ddjfdlkfje"), user);
    }

    @Test
    void getUserTest_NullUserId() {
        assertThrows(GetUserException.class, () -> {
            userService.findUser(null);
        });
    }

    @Test
    void getUserTest_BadUserId() {
        assertThrows(GetUserException.class, () -> {
            userService.findUser("ddjfdlkfje");
        });
    }

    @Test
    void deleteAvatarTest_Pass() {
        Mockito.when(userRepository.getUserById("ddjfdlkfje")).thenReturn(user);
        assertEquals(userService.deleteAvatar("ddjfdlkfje"), user);
    }

    @Test
    void deleteAvatarTest_NullUserId() {
        assertThrows(DeleteAvatarException.class, () -> {
            userService.deleteAvatar(null);
        });
    }

    @Test
    void deleteAvatarTest_BadUserId() {
        assertThrows(DeleteAvatarException.class, () -> {
            userService.deleteAvatar("ddjfdlkfje");
        });
    }

    @Test
    void updateAvatarTest_Pass() {
        Mockito.when(userRepository.getUserById("ddjfdlkfje")).thenReturn(user);
        assertEquals(userService.updateAvatar(new MyAccountChange(
                        "",
                        "",
                        "",
                        "",
                        new ByteArray(1),
                        "ddjfdlkfje")),
                user);
    }

    @Test
    void updateAvatarTest_BadUserId() {
        assertThrows(UpdateAvatarException.class, () -> {
            userService.updateAvatar(new MyAccountChange(
                    "",
                    "",
                    "",
                    "",
                    new ByteArray(1),
                    "ddjfdlkfje"));
        });
    }

    @Test
    void updateAvatarTest_NullUserId() {
        assertThrows(UpdateAvatarException.class, () -> {
            userService.updateAvatar(new MyAccountChange(
                    "",
                    "",
                    "",
                    "",
                    new ByteArray(1),
                    ""));
        });
    }

    @Test
    void updateAvatarTest_NullAvatar() {
        Mockito.when(userRepository.getUserById("ddjfdlkfje")).thenReturn(user);
        assertThrows(UpdateAvatarException.class, () -> {
            userService.updateAvatar(new MyAccountChange(
                    "",
                    "",
                    "",
                    "",
                    null,
                    "ddjfdlkfje"));
        });
    }

    @Test
    void updateNickNameTest_Pass() {
        Mockito.when(userRepository.getUserById("ddjfdlkfje")).thenReturn(user);
        assertEquals(userService.updateNickName(new MyAccountChange(
                        "",
                        "",
                        "",
                        "asadadwqa",
                        new ByteArray(0),
                        "ddjfdlkfje")),
                user);
    }

    @Test
    void updateNickNameTest_BadUserId() {
        assertThrows(UpdateNickNameException.class, () -> {
            userService.updateNickName(new MyAccountChange(
                    "",
                    "",
                    "",
                    "dsdsdsd",
                    new ByteArray(0),
                    "ddjfdlkfje"));
        });
    }

    @Test
    void updateNickNameTest_NullUserId() {
        assertThrows(UpdateNickNameException.class, () -> {
            userService.updateNickName(new MyAccountChange(
                    "",
                    "",
                    "",
                    "dsdsdsd",
                    new ByteArray(0),
                    ""));
        });
    }

    @Test
    void updateNickNameTest_NullAvatar() {
        Mockito.when(userRepository.getUserById("ddjfdlkfje")).thenReturn(user);
        assertThrows(UpdateNickNameException.class, () -> {
            userService.updateNickName(new MyAccountChange(
                    "",
                    "",
                    "",
                    null,
                    new ByteArray(0),
                    "ddjfdlkfje"));
        });
    }

    @Test
    void updateEmailTest_Pass() {
        Mockito.when(userRepository.getUserById("ddjfdlkfje")).thenReturn(user);
        assertEquals(userService.updateEmail(new MyAccountChange(
                        "vladik@ukr.net",
                        "",
                        "",
                        "",
                        new ByteArray(0),
                        "ddjfdlkfje")),
                user);
    }

    @Test
    void updateEmailTest_BadUserId() {
        assertThrows(UpdateEmailException.class, () -> {
            userService.updateEmail(new MyAccountChange(
                    "vladik@ukr.net",
                    "",
                    "",
                    "",
                    new ByteArray(0),
                    "ddjfdlkfje"));
        });
    }

    @Test
    void updateEmailTest_NullUserId() {
        assertThrows(UpdateEmailException.class, () -> {
            userService.updateEmail(new MyAccountChange(
                    "vladik@ukr.net",
                    "",
                    "",
                    "",
                    new ByteArray(0),
                    ""));
        });
    }

    @Test
    void updateEmailTest_NullEmail() {
        Mockito.when(userRepository.getUserById("ddjfdlkfje")).thenReturn(user);
        assertThrows(UpdateEmailException.class, () -> {
            userService.updateEmail(new MyAccountChange(
                    null,
                    "",
                    "",
                    "",
                    new ByteArray(0),
                    "ddjfdlkfje"));
        });
    }

    @Test
    void updatePasswordTest_Pass() {
        Mockito.when(userRepository.getUserById("ddjfdlkfje")).thenReturn(user);
        assertEquals(userService.updatePassword(new MyAccountChange(
                        "",
                        "Password5",
                        "",
                        "",
                        new ByteArray(0),
                        "ddjfdlkfje")),
                user);
    }

    @Test
    void updatePasswordTest_BadUserId() {
        assertThrows(UpdatePasswordExсeption.class, () -> {
            userService.updatePassword(new MyAccountChange(
                    "",
                    "Password5",
                    "",
                    "dsdsdsd",
                    new ByteArray(0),
                    "ddjfdlkfje"));
        });
    }

    @Test
    void updatePasswordTest_NullUserId() {
        assertThrows(UpdatePasswordExсeption.class, () -> {
            userService.updatePassword(new MyAccountChange(
                    "",
                    "Password5",
                    "",
                    "dsdsdsd",
                    new ByteArray(0),
                    ""));
        });
    }

    @Test
    void updatePasswordTest_NullPassword() {
        Mockito.when(userRepository.getUserById("ddjfdlkfje")).thenReturn(user);
        assertThrows(UpdatePasswordExсeption.class, () -> {
            userService.updatePassword(new MyAccountChange(
                    "",
                    null,
                    "",
                    "dsdsdsd",
                    new ByteArray(0),
                    "ddjfdlkfje"));
        });
    }
}
