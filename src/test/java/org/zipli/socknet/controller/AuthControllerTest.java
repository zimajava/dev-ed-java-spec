package org.zipli.socknet.controller;

import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthControllerTest {
    private LoginRequest loginRequest;
    private SignupRequest signupRequest;
    private SignupRequest signupRequest1;

    @Autowired
    AuthController authController;

    @MockBean
    JavaMailSender javaMailSender;

    @MockBean
    UserRepository userRepository;

    @BeforeEach
    public void init() {
        signupRequest = new SignupRequest(
                "newUser@gmail.com",
                "ugyur",
                "uyfrjjj",
                "gfr53");

        signupRequest1 = new SignupRequest(
                "registeredUser@gmail.com",
                "ugyur",
                "uyfrjjj",
                "gfr53");

        loginRequest = new LoginRequest(
                "registeredUser@gmail.com",
                "ugyur");
    }

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
    void authenticateUser_shouldReturnStatusOk() {
        assertTrue(authController.authenticateUser(loginRequest)
                                 .equals(ResponseEntity.ok("Here will be JwtResponse")));

    }
}
