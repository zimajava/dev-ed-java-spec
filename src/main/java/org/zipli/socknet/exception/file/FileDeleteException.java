package org.zipli.socknet.exception.file;

import org.zipli.socknet.exception.WsException;

public class FileDeleteException extends RuntimeException {

    private final WsException exceptionCode;

    public FileDeleteException(String message, WsException exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public int getNumberException() {
        return exceptionCode.getNumberException();
    }
}
