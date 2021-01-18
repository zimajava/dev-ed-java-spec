package org.zipli.socknet.exception.message;

import org.zipli.socknet.exception.WsException;

public class MessageSendException extends RuntimeException {

    private final WsException exceptionCode;

    public MessageSendException(String message, WsException exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public int getNumberException() {
        return exceptionCode.getNumberException();
    }
}
