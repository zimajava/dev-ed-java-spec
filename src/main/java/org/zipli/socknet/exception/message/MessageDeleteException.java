package org.zipli.socknet.exception.message;

import org.zipli.socknet.exception.ErrorStatusCode;

public class MessageDeleteException extends RuntimeException {
    private final ErrorStatusCode errorStatusCode;

    public MessageDeleteException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
