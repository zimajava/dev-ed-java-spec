package org.zipli.socknet.exception;

public class GetUserException extends RuntimeException {

    public GetUserException(int exceptionCode) {
    }

    public GetUserException(String message) {
        super(message);
    }
}
