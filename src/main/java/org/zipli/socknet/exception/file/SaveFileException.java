package org.zipli.socknet.exception.file;

import org.zipli.socknet.exception.ErrorStatusCodeWs;

public class SaveFileException  extends RuntimeException {

    private final ErrorStatusCodeWs exceptionCode;

    public SaveFileException(String message, ErrorStatusCodeWs exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public int getNumberException() {
        return exceptionCode.getNumberException();
    }
}
