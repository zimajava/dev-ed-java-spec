package org.zipli.socknet.exception;

public class AuthException extends RuntimeException {

    private ErrorStatusCode errorStatusCode;

    public AuthException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
