package org.zipli.socknet.exception.video;

import org.zipli.socknet.exception.ErrorStatusCode;

public class VideoCallException extends RuntimeException {
    private final ErrorStatusCode numberException;

    public VideoCallException( ErrorStatusCode numberException) {
        this.numberException = numberException;
    }

    public ErrorStatusCode getNumberException() {
        return numberException;
    }
}
