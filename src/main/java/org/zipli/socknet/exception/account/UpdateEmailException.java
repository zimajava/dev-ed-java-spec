package org.zipli.socknet.exception.account;

public class UpdateEmailException extends RuntimeException {

    private ErrorStatusCode errorStatusCode;

    public UpdateEmailException(ErrorStatusCode errorStatusCode) {
    this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }

}

