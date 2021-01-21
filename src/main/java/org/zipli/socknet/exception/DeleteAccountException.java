package org.zipli.socknet.exception;

public class DeleteAccountException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public DeleteAccountException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
