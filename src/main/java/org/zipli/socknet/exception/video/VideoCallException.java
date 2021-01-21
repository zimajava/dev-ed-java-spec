package org.zipli.socknet.exception.video;

public class VideoCallException extends RuntimeException {
    private final int numberException;

    public VideoCallException(String message, int numberException) {
        super(message);
        this.numberException = numberException;
    }

    public long getNumberException() {
        return numberException;
    }
}
