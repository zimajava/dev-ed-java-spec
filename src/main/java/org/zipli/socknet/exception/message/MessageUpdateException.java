package org.zipli.socknet.exception.message;

import org.zipli.socknet.exception.ErrorStatusCodeWs;

public class MessageUpdateException extends RuntimeException {

    private final ErrorStatusCodeWs exceptionCode;

    public MessageUpdateException(String message, ErrorStatusCodeWs exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public int getNumberException() {
        return exceptionCode.getNumberException();
    }
}
