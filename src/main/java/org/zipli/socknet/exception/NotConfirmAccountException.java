package org.zipli.socknet.exception;

public class NotConfirmAccountException extends RuntimeException {

    private ErrorStatusCode errorStatusCode;

    public NotConfirmAccountException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
