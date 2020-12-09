package org.zipli.socknet.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.zipli.socknet.model.User;
import org.zipli.socknet.payload.request.SignupRequest;
import org.zipli.socknet.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthControllerTest {

    @Autowired
    AuthController authController;

    @MockBean
    JavaMailSender javaMailSender;

    @MockBean
    UserRepository userRepository;

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
    void authenticateUser() {
    }
}
