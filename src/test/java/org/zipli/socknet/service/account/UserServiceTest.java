package org.zipli.socknet.service.account;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.exception.account.*;
import org.zipli.socknet.model.User;
import org.zipli.socknet.payload.request.AvatarRequest;
import org.zipli.socknet.payload.request.EmailRequest;
import org.zipli.socknet.payload.request.NickNameRequest;
import org.zipli.socknet.payload.request.PasswordRequest;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.service.email.EmailConfirmationService;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


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
        assertEquals(userService.updateAvatar(new AvatarRequest("ddjfdlkfje", new byte[1])), user);
    }

    @Test
    void updateAvatarTest_BadUserId() {
        assertThrows(UpdateAvatarException.class, () -> {
            userService.updateAvatar(new AvatarRequest("ddjfdlkfje", new byte[1]));
        });
    }

    @Test
    void updateAvatarTest_NullUserId() {
        assertThrows(UpdateAvatarException.class, () -> {
            userService.updateAvatar(new AvatarRequest(null, new byte[1]));
        });
    }

    @Test
    void updateAvatarTest_NullAvatar() {
        Mockito.when(userRepository.getUserById("ddjfdlkfje")).thenReturn(user);
        assertThrows(UpdateAvatarException.class, () -> {
            userService.updateAvatar(new AvatarRequest("ddjfdlkfje", null));
        });
    }

    @Test
    void updateNickNameTest_Pass() {
        Mockito.when(userRepository.getUserById("ddjfdlkfje")).thenReturn(user);
        assertEquals(userService.updateNickName(new NickNameRequest("ddjfdlkfje", "vladil-12")),
                user);
    }

    @Test
    void updateNickNameTest_BadUserId() {
        assertThrows(UpdateNickNameException.class, () -> {
            userService.updateNickName(new NickNameRequest("ddjfdlkfje", "vladil-12"));
        });
    }

    @Test
    void updateNickNameTest_NullUserId() {
        assertThrows(UpdateNickNameException.class, () -> {
            userService.updateNickName(new NickNameRequest(null, "vladil-12"));
        });
    }

    @Test
    void updateNickNameTest_NullNickName() {
        Mockito.when(userRepository.getUserById("ddjfdlkfje")).thenReturn(user);
        assertThrows(UpdateNickNameException.class, () -> {
            userService.updateNickName(new NickNameRequest("ddjfdlkfje", null));
        });
    }

    @Test
    void updateEmailTest_Pass()  {
        Mockito.when(userRepository.getUserById("ddjfdlkfje")).thenReturn(user);
        assertEquals(userService.updateEmail(new EmailRequest("ddjfdlkfje", "vlad3415@ukr.net")),
                user);
    }

    @Test
    void updateEmailTest_BadUserId() {
        assertThrows(UpdateEmailException.class, () -> {
            userService.updateEmail(new EmailRequest("ddjfdlkfje", "vlad345@ukr.net"));
        });
    }

    @Test
    void updateEmailTest_NullUserId() {
        assertThrows(UpdateEmailException.class, () -> {
            userService.updateEmail(new EmailRequest(null , "vlad345@ukr.net"));
        });
    }

    @Test
    void updateEmailTest_NullEmail() {
        Mockito.when(userRepository.getUserById("ddjfdlkfje")).thenReturn(user);
        assertThrows(UpdateEmailException.class, () -> {
            userService.updateEmail(new EmailRequest("ddjfdlkfje", null));
        })
    }

    @Test
    void updateEmailTest_DoubleEmail() {
        Mockito.when(userRepository.getUserById("ddjfdlkfje")).thenReturn(user);
        Mockito.when(userRepository.getUserByEmail("Vlad@ukr.net")).thenReturn(user);
        assertThrows(UpdateEmailException.class, () -> {
            userService.updateEmail(new EmailRequest("ddjfdlkfje" , "Vlad@ukr.net"));
        });
    }

    @Test
    void updatePasswordTest_Pass() {
        Mockito.when(userRepository.getUserById("ddjfdlkfje")).thenReturn(user);
        assertEquals(userService.updatePassword(new PasswordRequest("ddjfdlkfje", "Password4")),
                user);
    }

    @Test
    void updatePasswordTest_BadUserId() {
        assertThrows(UpdatePasswordExсeption.class, () -> {
            userService.updatePassword(new PasswordRequest("ddjfdlkfje", "Password4"));
        });
    }

    @Test
    void updatePasswordTest_NullUserId() {
        assertThrows(UpdatePasswordExсeption.class, () -> {
            userService.updatePassword(new PasswordRequest(null, "Password4"));
        });
    }

    @Test
    void updatePasswordTest_NullPassword() {
        Mockito.when(userRepository.getUserById("ddjfdlkfje")).thenReturn(user);
        assertThrows(UpdatePasswordExсeption.class, () -> {
            userService.updatePassword(new PasswordRequest("ddjfdlkfje", null));
        });
    }

    @Test
    void deleteAccountTest_Pass() {
        Mockito.when(userRepository.getUserById("ddjfdlkfje")).thenReturn(user);
        assertEquals(userService.deleteAccount("ddjfdlkfje"), "ddjfdlkfje");
    }

    @Test
    void deleteAccountTest_NullUserId() {
        assertThrows(DeleteAccountException.class, () -> {
            userService.deleteAccount(null);
        });
    }

    @Test
    void deleteAccountTest_BadUserId() {
        assertThrows(DeleteAccountException.class, () -> {
            userService.deleteAccount("ddjfdlkfje");
        });
    }
}
