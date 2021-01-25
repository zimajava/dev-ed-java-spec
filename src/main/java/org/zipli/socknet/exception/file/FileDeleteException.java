package org.zipli.socknet.exception.file;

import org.zipli.socknet.exception.ErrorStatusCode;

public class FileDeleteException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public FileDeleteException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
