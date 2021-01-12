package org.zipli.socknet.exception;

public class VideoCallException extends RuntimeException{
    private int exceptionCode;

    public VideoCallException(int exceptionCode) {
        this.exceptionCode = exceptionCode;
    }
}
