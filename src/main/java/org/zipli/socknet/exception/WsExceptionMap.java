package org.zipli.socknet.exception;

public enum WsExceptionMap {
    CHAT_ACCESS_ERROR(21),
    MESSAGE_ACCESS_ERROR(22),
    ALREADY_EXISTS(23),
    CHAT_NOT_EXIT(24),
    MESSAGE_NOT_EXIT(25),
    UNEXPECTED_EXCEPTION(26),
    USER_NOT_FOUND_EXCEPTION(27);

    private final long numberException;

    WsExceptionMap(long numberException) {
        this.numberException = numberException;
    }

    public long getNumberException() {
        return numberException;
    }
}
