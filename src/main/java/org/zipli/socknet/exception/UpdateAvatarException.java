package org.zipli.socknet.exception;

public class UpdateAvatarException extends RuntimeException {

    private ErrorStatusCode errorStatusCode;

    public UpdateAvatarException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
