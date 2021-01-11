package org.zipli.socknet.exception;

public class NotConfirmAccountException extends RuntimeException {
    public NotConfirmAccountException(int exceptionCode) {
    }

    public NotConfirmAccountException(String message) {
        super(message);
    }
}
