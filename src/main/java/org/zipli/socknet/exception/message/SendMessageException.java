package org.zipli.socknet.exception.message;

import org.zipli.socknet.exception.ErrorStatusCode;

public class SendMessageException extends Throwable {
    private final ErrorStatusCode errorStatusCode;

    public SendMessageException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getNumberException() {
        return errorStatusCode;
    }
}