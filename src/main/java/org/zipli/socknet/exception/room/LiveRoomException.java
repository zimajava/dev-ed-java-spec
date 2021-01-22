package org.zipli.socknet.exception.room;

import org.zipli.socknet.exception.ErrorStatusCode;

public class LiveRoomException extends Throwable {

    private final ErrorStatusCode errorStatusCodeRoom;

    public LiveRoomException(ErrorStatusCode errorStatusCodeRoom) {
        this.errorStatusCodeRoom = errorStatusCodeRoom;
    }

    public ErrorStatusCode getErrorStatusCodeRoom() {
        return errorStatusCodeRoom;
    }
}
