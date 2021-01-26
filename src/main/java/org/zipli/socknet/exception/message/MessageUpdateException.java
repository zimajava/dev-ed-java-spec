package org.zipli.socknet.exception.message;

import org.zipli.socknet.exception.ErrorStatusCode;

public class MessageUpdateException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public MessageUpdateException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
