package org.zipli.socknet.exception.chat;

public class DeleteChatException extends RuntimeException{

    private final long numberException;

    public DeleteChatException(String message, long numberException) {
        super(message);
        this.numberException = numberException;
    }

    public long getNumberException() {
        return numberException;
    }
}
