package org.zipli.socknet.exception;

public enum WsException {
    CHAT_ACCESS_ERROR(101),
    MESSAGE_ACCESS_ERROR(102),
    ALREADY_EXISTS(103),
    CHAT_NOT_EXISTS(104),
    MESSAGE_NOT_EXISTS(105),
    UNEXPECTED_EXCEPTION(106),
    USER_NOT_FOUND_EXCEPTION(107),
    CHAT_NOT_FOUND_EXCEPTION(108),
    VIDEO_CALL_EXCEPTION(109),
    FILE_ACCESS_ERROR(110),
    GRIDFSFILE_IS_NOT_FOUND(111),
    FILE_WAS_NOT_LOADING_CORRECT(112),
    FILE_IS_NOT_IN_A_DB(113),
    SEARCH_BY_PARAMS_ERROR(114);

    private final int numberException;

    WsException(int numberException) {
        this.numberException = numberException;
    }

    public int getNumberException() {
        return numberException;
    }
}
