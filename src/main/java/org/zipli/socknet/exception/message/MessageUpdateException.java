package org.zipli.socknet.exception.message;

public class MessageUpdateException extends RuntimeException{

    private final long numberException;

    public MessageUpdateException(String message, long numberException) {
        super(message);
        this.numberException = numberException;
    }

    public long getNumberException() {
        return numberException;
    }
}
