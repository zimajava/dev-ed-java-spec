package org.zipli.socknet.exception.auth;

import org.zipli.socknet.exception.ErrorStatusCode;

public class AuthException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public AuthException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
