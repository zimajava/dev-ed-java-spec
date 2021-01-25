package org.zipli.socknet.exception.account;

import org.zipli.socknet.exception.ErrorStatusCode;

public class UpdateEmailException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public UpdateEmailException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }

}

