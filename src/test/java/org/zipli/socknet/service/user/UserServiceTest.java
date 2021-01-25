package org.zipli.socknet.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.zipli.socknet.dto.request.AvatarRequest;
import org.zipli.socknet.dto.request.EmailRequest;
import org.zipli.socknet.dto.request.NickNameRequest;
import org.zipli.socknet.dto.request.PasswordRequest;
import org.zipli.socknet.exception.SearchByParamsException;
import org.zipli.socknet.exception.account.*;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.repository.model.User;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserServiceTest {

    private final String email = "email@gmail.com";
    private final String userName = "Valve";
    private final String nickName = "Nicki";
    private final String password = "qwerty";
    private final String avatar = "avatar";
    private final String searchParam = "Val";

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    EmailConfirmationService emailConfirmationService;

    User user = new User(email, password, userName, nickName);

    @BeforeEach
    void setUp() {
        user.setId("ddjfdlkfje");
    }

    @Test
    void getUserTest_Pass() {
        Mockito.when(userRepository.getUserById(user.getId())).thenReturn(user);
        assertEquals(userService.findUser(user.getId()), user);
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
            userService.findUser(user.getId());
        });
    }

    @Test
    void deleteAvatarTest_Pass() {
        Mockito.when(userRepository.getUserById(user.getId())).thenReturn(user);
        assertEquals(userService.deleteAvatar(user.getId()), user);
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
            userService.deleteAvatar(user.getId());
        });
    }

    @Test
    void updateAvatarTest_Pass() {
        Mockito.when(userRepository.getUserById(user.getId())).thenReturn(user);
        assertEquals(userService.updateAvatar(new AvatarRequest(user.getId(), avatar)), user);
    }

    @Test
    void updateAvatarTest_BadUserId() {
        assertThrows(UpdateAvatarException.class, () -> {
            userService.updateAvatar(new AvatarRequest(user.getId(), avatar));
        });
    }

    @Test
    void updateAvatarTest_NullUserId() {
        assertThrows(UpdateAvatarException.class, () -> {
            userService.updateAvatar(new AvatarRequest(null, avatar));
        });
    }

    @Test
    void updateAvatarTest_NullAvatar() {
        Mockito.when(userRepository.getUserById(user.getId())).thenReturn(user);
        assertThrows(UpdateAvatarException.class, () -> {
            userService.updateAvatar(new AvatarRequest(user.getId(), null));
        });
    }

    @Test
    void updateNickNameTest_Pass() {
        Mockito.when(userRepository.getUserById(user.getId())).thenReturn(user);
        assertEquals(userService.updateNickName(new NickNameRequest(user.getId(), "vladil-12")),
                user);
    }

    @Test
    void updateNickNameTest_BadUserId() {
        assertThrows(UpdateNickNameException.class, () -> {
            userService.updateNickName(new NickNameRequest(user.getId(), "vladil-12"));
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
        Mockito.when(userRepository.getUserById(user.getId())).thenReturn(user);
        assertThrows(UpdateNickNameException.class, () -> {
            userService.updateNickName(new NickNameRequest(user.getId(), null));
        });
    }

    @Test
    void updateEmailTest_Pass() {
        Mockito.when(userRepository.getUserById(user.getId())).thenReturn(user);
        assertEquals(userService.updateEmail(new EmailRequest(user.getId(), "vlad3415@ukr.net")),
                user);
    }

    @Test
    void updateEmailTest_BadUserId() {
        assertThrows(UpdateEmailException.class, () -> {
            userService.updateEmail(new EmailRequest(user.getId(), "vlad345@ukr.net"));
        });
    }

    @Test
    void updateEmailTest_NullUserId() {
        assertThrows(UpdateEmailException.class, () -> {
            userService.updateEmail(new EmailRequest(null, "vlad345@ukr.net"));
        });
    }

    @Test
    void updateEmailTest_NullEmail() {
        Mockito.when(userRepository.getUserById(user.getId())).thenReturn(user);
        assertThrows(UpdateEmailException.class, () -> {
            userService.updateEmail(new EmailRequest(user.getId(), null));
        });
    }

    @Test
    void updateEmailTest_DoubleEmail() {
        Mockito.when(userRepository.getUserById(user.getId())).thenReturn(user);
        Mockito.when(userRepository.getUserByEmail("Vlad@ukr.net")).thenReturn(user);
        assertThrows(UpdateEmailException.class, () -> {
            userService.updateEmail(new EmailRequest(user.getId(), "Vlad@ukr.net"));
        });
    }

    @Test
    void updatePasswordTest_Pass() {
        Mockito.when(userRepository.getUserById(user.getId())).thenReturn(user);
        assertEquals(userService.updatePassword(new PasswordRequest(user.getId(), "Password4")),
                user);
    }

    @Test
    void updatePasswordTest_BadUserId() {
        assertThrows(UpdatePasswordException.class, () -> {
            userService.updatePassword(new PasswordRequest(user.getId(), "Password4"));
        });
    }

    @Test
    void updatePasswordTest_NullUserId() {
        assertThrows(UpdatePasswordException.class, () -> {
            userService.updatePassword(new PasswordRequest(null, "Password4"));
        });
    }

    @Test
    void updatePasswordTest_NullPassword() {
        Mockito.when(userRepository.getUserById(user.getId())).thenReturn(user);
        assertThrows(UpdatePasswordException.class, () -> {
            userService.updatePassword(new PasswordRequest(user.getId(), null));
        });
    }

    @Test
    void deleteAccountTest_Pass() {
        Mockito.when(userRepository.getUserById(user.getId())).thenReturn(user);
        assertEquals(userService.deleteAccount(user.getId()), user.getId());
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
            userService.deleteAccount(user.getId());
        });
    }

    @Test
    void getUsersBySearchParam_Pass() {
        User secondUser = new User("em@g.com", "pass", "Value", "Nick");
        List<User> list = Stream.of(user, secondUser).collect(Collectors.toList());

        Mockito.when(userRepository.findUsersBySearchParam(searchParam))
                .thenReturn(List.of(user, secondUser));

        assertEquals(list, userService.getUsersBySearchParam(searchParam));
    }

    @Test
    void getUsersBySearchParam_Null_Param() {
        assertThrows(SearchByParamsException.class, () -> {
            userService.getUsersBySearchParam(null);
        });
    }

    @Test
    void getUsersBySearchParam_Shorter_Three_Chars() {
        assertThrows(SearchByParamsException.class, () -> {
            userService.getUsersBySearchParam("nl");
        });
    }

    @Test
    void getUsersBySearchParam_Users_Exist() {
        assertThrows(SearchByParamsException.class, () -> {
            userService.getUsersBySearchParam(searchParam);
            Mockito.when(userRepository.findUsersBySearchParam(searchParam)).thenReturn(null);
        });
    }
}
