package org.zipli.socknet.exception.chat;

public class JoinChatException extends RuntimeException {

    private final int numberException;

    public JoinChatException(String message, int numberException) {
        super(message);
        this.numberException = numberException;
    }

    public long getNumberException() {
        return numberException;
    }
}
