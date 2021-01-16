package org.zipli.socknet.exception.file;

import org.zipli.socknet.exception.WsException;

public class SaveFileException  extends RuntimeException {

    private final WsException exceptionCode;

    public SaveFileException(String message, WsException exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public int getNumberException() {
        return exceptionCode.getNumberException();
    }
}
