package org.zipli.socknet.exception.account;

import org.zipli.socknet.exception.ErrorStatusCode;

public class UpdateNickNameException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public UpdateNickNameException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }

}

