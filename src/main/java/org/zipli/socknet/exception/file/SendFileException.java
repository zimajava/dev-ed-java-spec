package org.zipli.socknet.exception.file;

import org.zipli.socknet.exception.ErrorStatusCodeWs;

public class SendFileException extends RuntimeException {

    private final ErrorStatusCodeWs exceptionCode;

    public SendFileException(String message, ErrorStatusCodeWs exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public int getNumberException() {
        return exceptionCode.getNumberException();
    }
}