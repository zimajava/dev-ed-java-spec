package org.zipli.socknet.exception.message;

public class MessageUpdateException extends RuntimeException{

    private final int numberException;

    public MessageUpdateException(String message, int numberException) {
        super(message);
        this.numberException = numberException;
    }

    public long getNumberException() {
        return numberException;
    }
}
