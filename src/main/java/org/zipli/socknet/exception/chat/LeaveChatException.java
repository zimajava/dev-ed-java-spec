package org.zipli.socknet.exception.chat;

import org.zipli.socknet.exception.ErrorStatusCode;

public class LeaveChatException extends RuntimeException {

    private final ErrorStatusCode numberException;

    public LeaveChatException(ErrorStatusCode numberException) {
        this.numberException = numberException;
    }

    public ErrorStatusCode getNumberException() {
        return numberException;
    }
}