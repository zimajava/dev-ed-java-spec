package org.zipli.socknet.exception;

public class DeleteAvatarException extends RuntimeException {

    private ErrorStatusCode errorStatusCode;

    public DeleteAvatarException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
