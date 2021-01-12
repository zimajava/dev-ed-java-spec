package org.zipli.socknet.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.zipli.socknet.exception.auth.NotConfirmAccountException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.service.email.EmailConfirmationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class EmailConfirmationServiceTest {

    @Autowired
    EmailConfirmationService emailConfirmationService;

    @MockBean
    JwtUtils jwtUtils;

    @MockBean
    UserRepository userRepository;

    @Test
    void confirmAccount_AccountOfUserIsOk() {
        String token = "qwerty";
        String username = "";

        Mockito.doReturn(username)
                .when(jwtUtils)
                .getUserNameFromJwtToken(token);
        Mockito.doReturn(new User())
                .when(userRepository)
                .getByUserName(username);

        assertEquals("Account verified", emailConfirmationService.confirmAccount(token));
    }

    @Test
    void confirmAccount_TokenIsInvalid() throws RuntimeException {
        String username = "";

        Mockito.doReturn(username)
                .when(jwtUtils)
                .getUserNameFromJwtToken(null);

        assertThrows(NotConfirmAccountException.class, () -> {
            emailConfirmationService.confirmAccount(null);
        });
    }
}
