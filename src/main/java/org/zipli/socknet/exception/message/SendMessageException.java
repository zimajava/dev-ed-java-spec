package org.zipli.socknet.exception.message;

import org.zipli.socknet.exception.ErrorStatusCode;

public class SendMessageException extends Throwable {
    private final ErrorStatusCode exceptionCode;

    public SendMessageException(ErrorStatusCode exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public ErrorStatusCode getNumberException() {
        return exceptionCode;
    }
}