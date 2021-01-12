package org.zipli.socknet.exception;

public enum WsExceptionMap {
    CHAT_ACCESS_ERROR(11),
    MESSAGE_ACCESS_ERROR(12),
    ALREADY_EXISTS(13),
    CHAT_NOT_EXIT(14),
    MESSAGE_NOT_EXIT(15),
    UNEXPECTED_EXCEPTION(16),
    USER_NOT_FOUND_EXCEPTION(17);

    private final long numberException;

    WsExceptionMap(long numberException) {
        this.numberException = numberException;
    }

    public long getNumberException() {
        return numberException;
    }
}
