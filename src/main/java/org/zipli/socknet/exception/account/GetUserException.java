package org.zipli.socknet.exception.account;

import org.zipli.socknet.exception.ErrorStatusCode;

public class GetUserException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public GetUserException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
