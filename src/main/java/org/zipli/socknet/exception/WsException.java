package org.zipli.socknet.exception;

public enum WsException {
    CHAT_ACCESS_ERROR(11),
    MESSAGE_ACCESS_ERROR(12),
    ALREADY_EXISTS(13),
    CHAT_NOT_EXIT(14),
    MESSAGE_NOT_EXIT(15),
    UNEXPECTED_EXCEPTION(16),
    USER_NOT_FOUND_EXCEPTION(17),
    CHAT_NOT_FOUND_EXCEPTION(18),
    VIDEO_CALL_EXCEPTION(19),
    FILE_ACCESS_ERROR(20),
    FILE_IS_NOT_IN_A_DB(21),
    FILE_NOT_FOUND(22);

    private final int numberException;

    WsException(int numberException) {
        this.numberException = numberException;
    }

    public int getNumberException() {
        return numberException;
    }
}
