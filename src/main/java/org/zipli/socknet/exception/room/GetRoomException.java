package org.zipli.socknet.exception.room;

import org.zipli.socknet.exception.ErrorStatusCode;

public class GetRoomException extends Throwable {

    private final ErrorStatusCode errorStatusCodeRoom;

    public GetRoomException(ErrorStatusCode errorStatusCodeRoom) {
        this.errorStatusCodeRoom = errorStatusCodeRoom;
    }

    public ErrorStatusCode getErrorStatusCodeRoom() {
        return errorStatusCodeRoom;
    }
}
