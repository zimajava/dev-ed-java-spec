package org.zipli.socknet.exception.file;

import org.zipli.socknet.exception.ErrorStatusCode;

public class SaveFileException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public SaveFileException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getNumberException() {
        return errorStatusCode;
    }
}
