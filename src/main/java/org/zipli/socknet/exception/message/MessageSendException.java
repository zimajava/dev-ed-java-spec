package org.zipli.socknet.exception.message;

import org.zipli.socknet.exception.ErrorStatusCode;

public class MessageSendException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public MessageSendException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getNumberException() {
        return errorStatusCode;
    }
}
