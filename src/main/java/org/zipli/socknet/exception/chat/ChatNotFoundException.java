package org.zipli.socknet.exception.chat;

public class ChatNotFoundException extends RuntimeException {
    private final int numberException;

    public ChatNotFoundException(String message, int numberException) {
        super(message);
        this.numberException = numberException;
    }

    public long getNumberException() {
        return numberException;
    }
}
