package org.zipli.socknet.service.password;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.zipli.socknet.exception.auth.UserNotFoundException;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.repository.model.User;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.security.services.UserDetailsImpl;
import org.zipli.socknet.service.user.ResetPasswordService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        User user = new User("email@gmail.com", "pasS1", "UsiN", "NickNamik");
        String token = "";

        Mockito.doReturn(user)
                .when(userRepository)
                .getUserByEmail(user.getEmail());

        Mockito.doReturn(user)
                .when(userRepository)
                .getUserByUserName(user.getUserName());

        Mockito.when(jwtUtils.generateJwtToken(new UserDetailsImpl(user), user.getEmail())).thenReturn(token);

        assertEquals(token, resetPasswordService.generateResetPasswordToken(user.getEmail()));
    }

    @Test
    void generateResetPasswordToken_UserIsNotFound() {
        assertThrows(UserNotFoundException.class, () -> {
            resetPasswordService.generateResetPasswordToken(email);
        });
    }

    @Test
    void resetPassword_TokenIsValid() {
        String token = "";
        String userName = "";
        String newPassword = "";

        Mockito.doReturn(userName)
                .when(jwtUtils)
                .getUserNameFromJwtToken(token);

        Mockito.doReturn(new User())
                .when(userRepository)
                .getUserByUserName(userName);

        assertEquals("Password successfully changed", resetPasswordService.resetPassword(newPassword, token));
    }
}
