package org.zipli.socknet.exception.chat;

import org.zipli.socknet.exception.ErrorStatusCode;

public class JoinChatException extends RuntimeException {

    private final ErrorStatusCode numberException;

    public JoinChatException(ErrorStatusCode numberException) {
        this.numberException = numberException;
    }

    public ErrorStatusCode getNumberException() {
        return numberException;
    }
}
