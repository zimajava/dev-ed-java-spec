package org.zipli.socknet.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.zipli.socknet.exception.AuthenticationException;
import org.zipli.socknet.models.User;
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
class AuthTokenFilterTest {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Test
    void doFilterInternalPass() {

        AuthTokenFilter filter = new AuthTokenFilter(jwtUtils, userDetailsService);

        UserDetails userDetails = new UserDetailsImpl(new User(1,
                "dsadasd",
                "dsadsad",
                "dasdasdasd",
                "dsad"));

        String jwtToken = jwtUtils.generateJwtToken(userDetails);

        HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockResp = Mockito.mock(HttpServletResponse.class);
        FilterChain mockFilterChain = Mockito.mock(FilterChain.class);

        Mockito.when(mockReq.getRequestURI()).thenReturn("/home");
        Mockito.when(mockReq.getParameter("jwt")).thenReturn(jwtToken);
        try {
            filter.doFilter(mockReq, mockResp, mockFilterChain);
        } catch (Exception e) {
            fail("should not throw an error");
            e.printStackTrace();
        }
    }

    @Test
    void doFilterInternalFail() {

        AuthTokenFilter filter = new AuthTokenFilter(jwtUtils, userDetailsService);

        HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockResp = Mockito.mock(HttpServletResponse.class);
        FilterChain mockFilterChain = Mockito.mock(FilterChain.class);

        Mockito.when(mockReq.getRequestURI()).thenReturn("/home");

        try {
            filter.doFilter(mockReq, mockResp, mockFilterChain);
            failAuthException();
        } catch ( ServletException | IOException e) {
            failAuthException();
        } catch (AuthenticationException e){
            assertEquals("Cannot set user authentication", e.getMessage());
        }
    }

    void failAuthException(){
        fail("AuthenticationException must be thrown");
    }
}