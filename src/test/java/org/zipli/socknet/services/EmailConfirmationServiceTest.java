package org.zipli.socknet.services;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.zipli.socknet.models.User;
import org.zipli.socknet.repositories.modelsRepositories.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.security.services.UserDetailsImpl;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class EmailConfirmationServiceTest {
    @Autowired
    EmailConfirmationService emailConfirmationService;

    User user = new User(100,
            "uhpuigti@gmail.com",
            "ugyur",
            "uyfrjjj",
            "gfr53");

    @Test
    void sendConfirmationEmail() {

        assertTrue(emailConfirmationService.sendConfirmationEmail(user)
                .equals(ResponseEntity.ok("User registered successfully!")));
    }
}
