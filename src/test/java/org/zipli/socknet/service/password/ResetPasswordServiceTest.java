package org.zipli.socknet.service.password;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    private String email;
    private String newPassword;

    @BeforeEach
    public void init() {
        String email = "registeredUser@gmail.com";
        String newPassword = "yrxxW245";
    }

    @Test
    void generateResetPasswordToken_UserIsRegisteredInDatabase() {
        String token = null;
        Mockito.doReturn(new User())
                .when(userRepository)
                .getUserByEmail(email);

        assertEquals(token, resetPasswordService.generateResetPasswordToken(email));
    }

    @Test
    void generateResetPasswordToken_UserIsNotFound() {
        assertThrows(UserNotFoundException.class, () -> {
            resetPasswordService.generateResetPasswordToken(email);
        });
    }

    @Test
    void resetPassword_TokenIsValid() {
        String token = "gfhrkxr";
        String userName = "ygbuiui";
        Mockito.doReturn(userName)
                .when(jwtUtils)
                .getUserNameFromJwtToken(token);
        Mockito.doReturn(new User("hgboi", "yrvr7", "ygbuiui", "uin"))
                .when(userRepository)
                .getByUserName(userName);

        assertEquals("Password successfully changed", resetPasswordService.resetPassword(newPassword, token));
    }
}
