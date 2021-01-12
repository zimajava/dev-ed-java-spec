package org.zipli.socknet.exception;

public class UserNotFoundException extends RuntimeException {

    private ErrorStatusCode errorStatusCode;

    public UserNotFoundException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
