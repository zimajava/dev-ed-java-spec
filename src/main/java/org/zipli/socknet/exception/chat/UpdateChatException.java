package org.zipli.socknet.exception.chat;

import org.zipli.socknet.exception.ErrorStatusCodeWs;

public class UpdateChatException extends RuntimeException {

    private final ErrorStatusCodeWs exceptionCode;

    public UpdateChatException(String message, ErrorStatusCodeWs exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public int getNumberException() {
        return exceptionCode.getNumberException();
    }
}
