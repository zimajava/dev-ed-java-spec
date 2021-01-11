package org.zipli.socknet.exception;

public class AuthException extends RuntimeException {

    public AuthException(int exceptionCode) {
    }

    public AuthException(String message) {
        super(message);
    }
}
