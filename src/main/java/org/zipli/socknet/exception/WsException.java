package org.zipli.socknet.exception;

public enum WsException {
    CHAT_ACCESS_ERROR(11),
    MESSAGE_ACCESS_ERROR(12),
    ALREADY_EXISTS(13),
    CHAT_NOT_EXISTS(14),
    MESSAGE_NOT_EXISTS(15),
    UNEXPECTED_EXCEPTION(16),
    USER_NOT_FOUND_EXCEPTION(17);

    private final int numberException;

    WsException(int numberException) {
        this.numberException = numberException;
    }

    public int getNumberException() {
        return numberException;
    }
}
