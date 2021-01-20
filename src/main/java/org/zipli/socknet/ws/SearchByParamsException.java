package org.zipli.socknet.ws;

import org.zipli.socknet.exception.ErrorStatusCode;

public class SearchByParamsException extends RuntimeException {
    
    private ErrorStatusCode errorStatusCode;

    public SearchByParamsException(ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
