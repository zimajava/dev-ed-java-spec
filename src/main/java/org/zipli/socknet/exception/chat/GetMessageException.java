package org.zipli.socknet.exception.chat;

import org.zipli.socknet.exception.ErrorStatusCode;

public class GetMessageException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public GetMessageException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}