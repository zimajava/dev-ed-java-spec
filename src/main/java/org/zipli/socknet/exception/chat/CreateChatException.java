package org.zipli.socknet.exception.chat;

import org.zipli.socknet.exception.ErrorStatusCode;

public class CreateChatException extends RuntimeException {

    private final ErrorStatusCode exceptionCode;

    public CreateChatException(ErrorStatusCode exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public ErrorStatusCode getNumberException() {
        return exceptionCode;
    }
}
