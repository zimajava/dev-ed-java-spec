package org.zipli.socknet.exception.chat;

import org.zipli.socknet.exception.ErrorStatusCode;

public class DeleteChatException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public DeleteChatException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
