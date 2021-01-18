package org.zipli.socknet.exception.message;

import org.zipli.socknet.exception.ErrorStatusCodeWs;

public class MessageSendException extends RuntimeException {

    private final ErrorStatusCodeWs exceptionCode;

    public MessageSendException(String message, ErrorStatusCodeWs exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public int getNumberException() {
        return exceptionCode.getNumberException();
    }
}
