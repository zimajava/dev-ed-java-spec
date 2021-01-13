package org.zipli.socknet.exception;

public class DeleteSessionException extends Throwable {

    private ErrorStatusCode errorStatusCode;

    public DeleteSessionException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
