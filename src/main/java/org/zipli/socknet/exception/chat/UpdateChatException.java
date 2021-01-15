package org.zipli.socknet.exception.chat;

public class UpdateChatException extends RuntimeException {

    private final int numberException;

    public UpdateChatException(String message, int numberException) {
        super(message);
        this.numberException = numberException;
    }

    public long getNumberException() {
        return numberException;
    }
}
