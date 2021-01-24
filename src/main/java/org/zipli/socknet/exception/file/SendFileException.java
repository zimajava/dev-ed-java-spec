package org.zipli.socknet.exception.file;

import org.zipli.socknet.exception.ErrorStatusCode;

public class SendFileException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public SendFileException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getNumberException() {
        return errorStatusCode;
    }
}