package org.zipli.socknet.exception.chat;

import org.zipli.socknet.exception.ErrorStatusCode;

public class JoinChatException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public JoinChatException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
