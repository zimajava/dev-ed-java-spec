package org.zipli.socknet.exception.auth;

import org.springframework.security.core.AuthenticationException;

public class AuthException extends RuntimeException {

    private int exceptionCode;

    public AuthException(int exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public AuthException(String message) {
        super(message);
    }
}
