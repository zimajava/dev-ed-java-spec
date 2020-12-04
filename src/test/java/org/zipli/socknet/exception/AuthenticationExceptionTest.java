package org.zipli.socknet.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationExceptionTest {

    private AuthenticationException authenticationException = new AuthenticationException();

    @Test
    void getMessage() {
        assertEquals("Cannot set user authentication",authenticationException.getMessage());
    }

    @Test
    void toString1() {
        assertEquals("AuthenticationException",authenticationException.toString());
    }
}