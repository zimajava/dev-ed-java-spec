package org.zipli.socknet.exception.auth;

import org.zipli.socknet.exception.ErrorStatusCode;

public class UserNotFoundException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public UserNotFoundException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
