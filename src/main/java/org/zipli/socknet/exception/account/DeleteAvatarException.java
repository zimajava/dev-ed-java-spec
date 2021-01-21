package org.zipli.socknet.exception.account;

import org.zipli.socknet.exception.ErrorStatusCode;

public class DeleteAvatarException extends RuntimeException {

    private final ErrorStatusCode errorStatusCode;

    public DeleteAvatarException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
