package org.zipli.socknet.exception;

public enum ErrorStatusCodeRoom {

    INCORRECT_REQUEST(1),
    ROOM_NOT_EXIT(2);

    private final int numberException;

    ErrorStatusCodeRoom(int numberException) {
        this.numberException = numberException;
    }

    public int getNumberException() {
        return numberException;
    }
}
