package org.zipli.socknet.exception.message;

public class MessageSendException extends RuntimeException {

    private final int numberException;

    public MessageSendException(String message, int numberException) {
        super(message);
        this.numberException = numberException;
    }

    public int getNumberException() {
        return numberException;
    }
}