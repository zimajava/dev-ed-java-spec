package org.zipli.socknet.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.zipli.socknet.model.User;
import org.zipli.socknet.payload.request.LoginRequest;
import org.zipli.socknet.payload.request.SignupRequest;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthControllerTest {

    @Autowired
    AuthController authController;

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
    void addUser() {
        Mockito.doReturn(new User())
                .when(userRepository)
                .getUserByEmail("registeredUser@gmail.com");

        assertTrue(authController.addUser(signupRequest1)
                .equals(ResponseEntity.badRequest()
                        .body("This email already exists!")));
        assertTrue(authController.addUser(signupRequest)
                .equals(ResponseEntity.ok("User registered successfully!")));
    }

    @Test
    void emailConfirm() {
        String token = "qwerty";
        String username = new String();
        Mockito.doReturn(username)
                .when(jwtUtils)
                .getUserNameFromJwtToken(token);
        Mockito.doReturn(new User())
                .when(userRepository)
                .getByUserName(username);

        assertTrue(authController.emailConfirm(token)
                .equals(ResponseEntity.ok("Account verified")));
    }

    @Test
    void authenticateUser() {
    }
}
