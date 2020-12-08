package org.zipli.socknet.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.zipli.socknet.exception.AuthenticationException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.security.services.UserDetailsImpl;
import org.zipli.socknet.security.services.UserDetailsServiceImpl;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
@SpringBootTest
class AuthTokenManagerTest {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Test
    void doFilterInternalPass() {

        AuthTokenManager filter = new AuthTokenManager(jwtUtils, userDetailsService);

        UserDetails userDetails = new UserDetailsImpl(
                new User(
                "dsadasd",
                "dsadsad",
                "dasdasdasd",
                "dsad"));

        String jwtToken = jwtUtils.generateJwtToken(userDetails);

        Authentication mockAuth = Mockito.mock(Authentication.class);

        Mockito.when(mockAuth.getCredentials()).thenReturn(jwtToken);
        try {
            filter.authenticate(mockAuth);
        } catch (Exception e) {
            fail("should not throw an error");
            e.printStackTrace();
        }
    }

    @Test
    void doFilterInternalFail() {

        AuthTokenManager filter = new AuthTokenManager(jwtUtils, userDetailsService);

        Authentication mockAuth = Mockito.mock(Authentication.class);

        Mockito.when(mockAuth.getCredentials()).thenReturn("/home");

        try {
            filter.authenticate(mockAuth);
            failAuthException();
        } catch (AuthenticationException e) {
            assertEquals("Cannot set user authentication", e.getMessage());
        }
    }

    void failAuthException() {
        fail("AuthenticationException must be thrown");
    }

}
