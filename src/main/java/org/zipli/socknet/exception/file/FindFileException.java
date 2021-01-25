package org.zipli.socknet.exception.file;

import org.zipli.socknet.exception.ErrorStatusCode;

public class FindFileException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public FindFileException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}