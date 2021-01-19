package org.zipli.socknet.exception.file;

import org.zipli.socknet.exception.WsException;

public class SendFileException extends RuntimeException {

    private final WsException exceptionCode;

    public SendFileException(String message, WsException exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public int getNumberException() {
        return exceptionCode.getNumberException();
    }
}