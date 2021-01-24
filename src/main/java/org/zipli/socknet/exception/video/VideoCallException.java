package org.zipli.socknet.exception.video;

import org.zipli.socknet.exception.ErrorStatusCode;

public class VideoCallException extends RuntimeException {
    private final ErrorStatusCode errorStatusCode;

    public VideoCallException( ErrorStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public ErrorStatusCode getErrorStatusCode() {
        return errorStatusCode;
    }
}
