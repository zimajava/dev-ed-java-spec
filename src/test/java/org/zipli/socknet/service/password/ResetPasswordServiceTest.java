package org.zipli.socknet.service.password;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.zipli.socknet.exception.InvalidTokenException;
import org.zipli.socknet.exception.UserNotFoundException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ResetPasswordServiceTest {

    @Autowired
    ResetPasswordService resetPasswordService;

    @MockBean
    JwtUtils jwtUtils;

    @MockBean
    UserRepository userRepository;

    @MockBean
    User user;

    @Test
    void generateResetPasswordToken_UserIsRegisteredInDatabase() {
        String email = "registeredUser@gmail.com";
        String token = null;
        Mockito.doReturn(true)
                .when(userRepository)
                .existsByEmail(email);
        Mockito.doReturn(new User())
                .when(userRepository)
                .getUserByEmail(email);

        assertEquals(token, resetPasswordService.generateResetPasswordToken(email));
    }

    @Test
    void generateResetPasswordToken_UserIsNotFound() {
        String email = "registeredUser@gmail.com";

        assertThrows(UserNotFoundException.class, () -> {
            resetPasswordService.generateResetPasswordToken(email);
        });
    }

    @Test
    void resetPassword_TokenIsValid() {
        String newPassword = "yrxxW245";
        String token = "gfhrkxr";
        String userName = "ygbuiui";
                Mockito.doReturn(userName)
                .when(jwtUtils)
                .getUserNameFromJwtToken(token);
        Mockito.doReturn(new User("hgboi","yrvr7", "ygbuiui","uin"))
                .when(userRepository)
                .getByUserName(userName);

        assertEquals("Password successfully changed", resetPasswordService.resetPassword(newPassword, token));
    }

    @Test
    void resetPassword_UserInNotInADB() {
        String newPassword = "yrxxW245";
        String token = "gfhrkxr";
        String userName = new String();
        Mockito.doReturn(userName)
                .when(jwtUtils)
                .getUserNameFromJwtToken(token);
        Mockito.doReturn(null)
                .when(userRepository)
                .getUserByEmail(userName);

        assertEquals(null, resetPasswordService.resetPassword(newPassword, token));
    }

    @Test
    void resetPassword_TokenIsInvalid() {
        String newPassword = "yrxxW245";
        String token = null;

        assertThrows(InvalidTokenException.class, () -> {
            resetPasswordService.resetPassword(newPassword, token);
        });
    }
}
