package org.zipli.socknet.security.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zipli.socknet.models.User;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.security.services.UserDetailsImpl;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilsTest {

    @Autowired
    JwtUtils jwtUtils;

    @Test
    void generateJwtToken() {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                new User(1,
                        "dsadasd",
                        "dsadsad",
                        "dasdasdasd",
                        "sas"));

        String jwt = jwtUtils.generateJwtToken(userDetails);

        assertTrue(jwtUtils.validateJwtToken(jwt));
    }

    @Test
    void getUserNameFromJwtToken() {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                new User(1,
                        "dsadasd",
                        "dsadsad",
                        "dasdasdasd",
                        "sas"));

        String jwt = jwtUtils.generateJwtToken(userDetails);
        String usernameByJwt = jwtUtils.getUserNameFromJwtToken(jwt);

        assertEquals(userDetails.getUsername(), usernameByJwt);
    }

    @Test
    void validateJwtTokenPass() {
        UserDetailsImpl userDetails = new UserDetailsImpl(new User(1,
                "dsadasd",
                "dsadsad",
                "dasdasdasd",
                "sas"));

        String jwtFalse = "noValidJwt";
        String jwtTry = jwtUtils.generateJwtToken(userDetails);

        assertFalse(jwtUtils.validateJwtToken(jwtFalse));
        assertTrue(jwtUtils.validateJwtToken(jwtTry));
    }

    @Test
    void validateJwtTokenFail() {
        String jwtFalse = "noValidJwt";

        assertFalse(jwtUtils.validateJwtToken(jwtFalse));
    }
}