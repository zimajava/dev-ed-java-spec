package org.zipli.socknet.exception;

public class UpdateAvatarException extends RuntimeException {
    public UpdateAvatarException(int exceptionCode) {
    }

    public UpdateAvatarException(String message) {
        super(message);
    }
}
