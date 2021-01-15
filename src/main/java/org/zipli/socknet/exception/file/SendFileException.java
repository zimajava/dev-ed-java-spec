package org.zipli.socknet.exception.file;

public class SendFileException extends RuntimeException {

    private final int numberException;

    public SendFileException(String message, int numberException) {
        super(message);
        this.numberException = numberException;
    }

    public int getNumberException() {
        return numberException;
    }
}