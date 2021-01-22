package org.zipli.socknet.exception.file;

import org.zipli.socknet.exception.ErrorStatusCode;

public class SendFileException extends RuntimeException {

    private final ErrorStatusCode exceptionCode;

    public SendFileException(ErrorStatusCode exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public ErrorStatusCode getNumberException() {
        return exceptionCode;
    }
}