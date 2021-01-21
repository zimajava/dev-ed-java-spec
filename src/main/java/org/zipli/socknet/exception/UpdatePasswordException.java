package org.zipli.socknet.exception;

public class UpdatePasswordException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public UpdatePasswordException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}

