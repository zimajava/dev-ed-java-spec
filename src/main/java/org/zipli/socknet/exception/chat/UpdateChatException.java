package org.zipli.socknet.exception.chat;

import org.zipli.socknet.exception.WsException;

public class UpdateChatException extends RuntimeException {

    private final WsException exceptionCode;

    public UpdateChatException(String message, WsException exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public int getNumberException() {
        return exceptionCode.getNumberException();
    }
}
