package org.zipli.socknet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.zipli.socknet.models.User;
import org.zipli.socknet.services.EmailConfirmationService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthControllerTest {

    @Autowired
    AuthController authController;

    User user = new User(100,
            "uhpuigti@gmail.com",
            "ugyur",
            "uyfrjjj",
            "gfr53");

    @Test
    void addUser() {
    }

    @Test
    void authenticateUser() {
    }

    @Test
    void sendConfirmationEmail() {

        assertTrue(authController.sendConfirmationEmail(user)
                .equals(ResponseEntity.ok("User registered successfully!")));
    }
}