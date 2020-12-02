package org.zipli.socknet.security.jwt;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zipli.socknet.security.services.UserDetailsImpl;
import org.zipli.socknet.security.services.UserDetailsServiceImpl;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@SpringBootTest
class AuthTokenFilterTest {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Test
    void doFilterInternal() {

        AuthTokenFilter filter = new AuthTokenFilter(jwtUtils,userDetailsService);

        HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockResp = Mockito.mock(HttpServletResponse.class);
        FilterChain mockFilterChain = Mockito.mock(FilterChain.class);

        Mockito.when(mockReq.getRequestURI()).thenReturn("/");
        Mockito.when(mockReq.getParameter("jwt")).thenReturn("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkYXNkYXNkYXNkIiwiaWF0IjoxNjA2OTIxMzEzLCJleHAiOjE2MDcwMDc3MTN9.QPGfBCVwg5SJ6y2HDONEnTjO7K1RLPWQvEjSdbbpNxtD1d_JLYQullURnh856NJAGSpahskHygGZn62eeg68-A");

        try {
            filter.doFilter(mockReq, mockResp, mockFilterChain);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }


    }
}