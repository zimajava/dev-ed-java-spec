package org.zipli.socknet.exception.chat;

import org.zipli.socknet.exception.ErrorStatusCode;


public class GetAllUsersException extends RuntimeException {

    private ErrorStatusCode errorStatusCode;

    public GetAllUsersException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}

