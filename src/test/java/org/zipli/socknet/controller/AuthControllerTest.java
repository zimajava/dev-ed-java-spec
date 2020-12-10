package org.zipli.socknet.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.zipli.socknet.exception.NotConfirmAccountException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.payload.request.LoginRequest;
import org.zipli.socknet.payload.request.SignupRequest;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.services.EmailConfirmationService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthControllerTest {

    @Autowired
    AuthController authController;

    @MockBean
    EmailConfirmationService emailConfirmationService;

    @MockBean
    JavaMailSender javaMailSender;

    @MockBean
    UserRepository userRepository;

    @MockBean
    LoginRequest loginRequest;

    @MockBean
    JwtUtils jwtUtils;

    SignupRequest signupRequest = new SignupRequest(
            "newUser@gmail.com",
            "ugyur",
            "uyfrjjj",
            "gfr53");

    SignupRequest signupRequest1 = new SignupRequest(
            "registeredUser@gmail.com",
            "ugyur",
            "uyfrjjj",
            "gfr53");

    @Test
    void addUser_UserIsOk() {

        assertEquals(authController.addUser(signupRequest), ResponseEntity.ok("User registered successfully!"));
    }

    @Test
    void addUser_UserHasAlreadyRegistered() {
        Mockito.doReturn(new User())
                .when(userRepository)
                .getUserByEmail("registeredUser@gmail.com");

        assertEquals(authController.addUser(signupRequest1), ResponseEntity.badRequest()
                .body("This email already exists!"));
    }

    @Test
    void emailConfirm_TokenIsValid() {
        String token = "qwerty";
        String username = new String();
        Mockito.doReturn(username)
                .when(jwtUtils)
                .getUserNameFromJwtToken(token);
        Mockito.doReturn(new User())
                .when(userRepository)
                .getByUserName(username);

        assertEquals(authController.emailConfirm(token), ResponseEntity.ok("Account verified"));
    }

    @Test
    void emailConfirm_TokenIsInvalid() {
        Mockito.doThrow(new NotConfirmAccountException("Error. The token is invalid or broken!"))
                .when(emailConfirmationService)
                .confirmAccount(null);
        NotConfirmAccountException e = new NotConfirmAccountException("Error. The token is invalid or broken!");

        assertEquals(ResponseEntity
                .badRequest()
                .body(e),
            authController.emailConfirm(null));
    }

    @Test
    void authenticateUser() {
    }
}
