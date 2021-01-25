package org.zipli.socknet.exception.chat;

import org.zipli.socknet.exception.ErrorStatusCode;

public class CreateChatException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public CreateChatException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
