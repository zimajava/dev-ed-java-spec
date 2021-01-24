package org.zipli.socknet.exception.chat;

import org.zipli.socknet.exception.ErrorStatusCode;

public class UpdateChatException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public UpdateChatException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getNumberException() {
        return errorStatusCode;
    }
}
