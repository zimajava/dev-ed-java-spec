package org.zipli.socknet.exception.chat;

import org.zipli.socknet.exception.ErrorStatusCode;

public class ChatNotFoundException extends RuntimeException {
    private final ErrorStatusCode numberException;

    public ChatNotFoundException(ErrorStatusCode numberException) {
        this.numberException = numberException;
    }

    public ErrorStatusCode getNumberException() {
        return numberException;
    }
}
