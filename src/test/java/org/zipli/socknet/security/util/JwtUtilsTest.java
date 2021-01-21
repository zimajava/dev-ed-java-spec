package org.zipli.socknet.security.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zipli.socknet.model.User;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.security.services.UserDetailsImpl;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilsTest {

    private final UserDetailsImpl userDetails = new UserDetailsImpl(
            new User("dsadasd",
                    "dsadsad",
                    "dasdasdasd",
                    "sas"));
    @Autowired
    JwtUtils jwtUtils;
    String email = "jkbuigt7";

    @Test
    void generateJwtToken() {
        String jwt = jwtUtils.generateJwtToken(userDetails, email);

        assertTrue(jwtUtils.validateJwtToken(jwt));
    }

    @Test
    void getUserNameFromJwtToken() {
        String jwt = jwtUtils.generateJwtToken(userDetails, email);
        String usernameByJwt = jwtUtils.getUserNameFromJwtToken(jwt);

        assertEquals(userDetails.getUsername(), usernameByJwt);
    }

    @Test
    void getEmailFromJwtToken() {
        String jwt = jwtUtils.generateJwtToken(userDetails, email);
        String emailByJwt = jwtUtils.getEmailFromJwtToken(jwt);

        assertEquals(email, emailByJwt);
    }

    @Test
    void validateJwtTokenPass() {
        String jwtFalse = "noValidJwt";
        String jwtTry = jwtUtils.generateJwtToken(userDetails, email);

        assertFalse(jwtUtils.validateJwtToken(jwtFalse));
        assertTrue(jwtUtils.validateJwtToken(jwtTry));
    }

    @Test
    void validateJwtTokenFail() {
        String jwtFalse = "noValidJwt";

        assertFalse(jwtUtils.validateJwtToken(jwtFalse));
    }
}
