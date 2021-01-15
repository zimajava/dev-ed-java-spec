package org.zipli.socknet.exception.file;

public class FileDeleteException extends RuntimeException {

    private final int numberException;

    public FileDeleteException(String message, int numberException) {
        super(message);
        this.numberException = numberException;
    }

    public int getNumberException() {
        return numberException;
    }
}
