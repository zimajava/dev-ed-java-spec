package org.zipli.socknet.exception.auth;

public class InvalidTokenException extends Throwable {
    public InvalidTokenException(String message) {
        super(message);
    }
}
