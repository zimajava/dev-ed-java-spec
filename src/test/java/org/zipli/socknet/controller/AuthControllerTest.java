package org.zipli.socknet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.zipli.socknet.models.User;
import org.zipli.socknet.payload.request.SignupRequest;
import org.zipli.socknet.services.EmailConfirmationService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthControllerTest {

    @Autowired
    AuthController authController;

    SignupRequest signupRequest = new SignupRequest(
            "uhpuigti@gmail.com",
            "ugyur",
            "uyfrjjj",
            "gfr53");

    @Test
    void addUser() {

        assertTrue(authController.addUser(signupRequest)
                .equals(ResponseEntity.ok("User registered successfully!")));
    }

    @Test
    void authenticateUser() {
    }
}
