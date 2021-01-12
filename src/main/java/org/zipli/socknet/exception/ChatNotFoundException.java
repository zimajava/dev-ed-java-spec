package org.zipli.socknet.exception;

public class ChatNotFoundException extends RuntimeException {
    private int exceptionCode;

    public ChatNotFoundException(int exceptionCode) {
        this.exceptionCode = exceptionCode;
    }
}
