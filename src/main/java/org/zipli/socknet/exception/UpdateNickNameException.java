package org.zipli.socknet.exception;

public class UpdateNickNameException extends RuntimeException {
    public UpdateNickNameException(int exceptionCode) {
    }

    public UpdateNickNameException(String message) {
        super(message);
    }
}

