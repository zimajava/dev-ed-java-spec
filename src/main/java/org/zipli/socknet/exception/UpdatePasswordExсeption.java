package org.zipli.socknet.exception;

public class UpdatePasswordExсeption extends RuntimeException {
    public UpdatePasswordExсeption(int exceptionCode) {
    }

    public UpdatePasswordExсeption(String message) {
        super(message);
    }
}

