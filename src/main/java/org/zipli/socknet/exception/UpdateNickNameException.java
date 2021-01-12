package org.zipli.socknet.exception;

public class UpdateNickNameException extends RuntimeException {

    private ErrorStatusCode errorStatusCode;

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }

    public UpdateNickNameException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

}

