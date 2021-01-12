package org.zipli.socknet.exception.chat;

public class JoinChatException extends RuntimeException {

    private final long numberException;

    public JoinChatException(String message, long numberException) {
        super(message);
        this.numberException = numberException;
    }

    public long getNumberException() {
        return numberException;
    }
}
