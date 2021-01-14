package org.zipli.socknet.exception.auth;

import org.zipli.socknet.exception.ErrorStatusCode;

public class NotConfirmAccountException extends RuntimeException {

    private ErrorStatusCode errorStatusCode;

    public NotConfirmAccountException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
