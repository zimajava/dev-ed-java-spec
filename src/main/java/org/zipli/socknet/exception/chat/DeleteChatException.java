package org.zipli.socknet.exception.chat;

import org.zipli.socknet.exception.ErrorStatusCode;

public class DeleteChatException extends RuntimeException {

    private final ErrorStatusCode numberException;

    public DeleteChatException(ErrorStatusCode numberException) {
        this.numberException = numberException;
    }

    public ErrorStatusCode getNumberException() {
        return numberException;
    }
}
