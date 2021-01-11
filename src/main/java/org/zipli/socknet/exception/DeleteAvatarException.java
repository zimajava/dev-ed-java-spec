package org.zipli.socknet.exception;

public class DeleteAvatarException extends RuntimeException {
    public DeleteAvatarException(int exceptionCode) {
    }

    public DeleteAvatarException(String message) {
        super(message);
    }
}
