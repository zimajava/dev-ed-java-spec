package org.zipli.socknet.exception.chat;

public class DeleteChatException extends RuntimeException {

    private final int numberException;

    public DeleteChatException(String message, int numberException) {
        super(message);
        this.numberException = numberException;
    }

    public long getNumberException() {
        return numberException;
    }
}
