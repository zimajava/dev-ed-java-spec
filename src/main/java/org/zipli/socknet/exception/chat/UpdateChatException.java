package org.zipli.socknet.exception.chat;

public class UpdateChatException extends RuntimeException{

    private final long numberException;

    public UpdateChatException(String message, long numberException) {
        super(message);
        this.numberException = numberException;
    }

    public long getNumberException() {
        return numberException;
    }
}
