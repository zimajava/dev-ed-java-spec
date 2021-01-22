package org.zipli.socknet.exception.chat;

import org.zipli.socknet.exception.ErrorStatusCode;

public class UpdateChatException extends RuntimeException {

    private final ErrorStatusCode exceptionCode;

    public UpdateChatException(ErrorStatusCode exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public ErrorStatusCode getNumberException() {
        return exceptionCode;
    }
}
