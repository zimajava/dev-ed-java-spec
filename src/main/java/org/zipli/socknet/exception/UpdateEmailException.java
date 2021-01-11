package org.zipli.socknet.exception;

public class UpdateEmailException extends RuntimeException {
    public UpdateEmailException(int exceptionCode) {
     }

    public UpdateEmailException(String message) {
        super(message);
    }
}

