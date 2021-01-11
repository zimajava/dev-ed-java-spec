package org.zipli.socknet.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(int exceptionCode) {
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
