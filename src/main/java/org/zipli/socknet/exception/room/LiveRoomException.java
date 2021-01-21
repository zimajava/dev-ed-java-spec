package org.zipli.socknet.exception.room;

import org.zipli.socknet.exception.ErrorStatusCodeRoom;

public class LiveRoomException extends Throwable {

    private final ErrorStatusCodeRoom errorStatusCodeRoom;

    public LiveRoomException(ErrorStatusCodeRoom errorStatusCodeRoom) {
        this.errorStatusCodeRoom = errorStatusCodeRoom;
    }

    public ErrorStatusCodeRoom getErrorStatusCodeRoom() {
        return errorStatusCodeRoom;
    }
}
