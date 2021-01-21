package org.zipli.socknet.exception;

public class GetUserException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public GetUserException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
