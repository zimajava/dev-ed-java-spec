package org.zipli.socknet.exception.auth;

public class NotConfirmAccountException extends RuntimeException {

    private ErrorStatusCode errorStatusCode;

    public NotConfirmAccountException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
