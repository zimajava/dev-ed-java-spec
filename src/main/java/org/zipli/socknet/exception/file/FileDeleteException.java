package org.zipli.socknet.exception.file;

import org.zipli.socknet.exception.ErrorStatusCode;

public class FileDeleteException extends RuntimeException {

    private final ErrorStatusCode exceptionCode;

    public FileDeleteException(ErrorStatusCode exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public ErrorStatusCode getNumberException() {
        return exceptionCode;
    }
}
