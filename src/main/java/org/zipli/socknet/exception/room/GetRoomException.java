package org.zipli.socknet.exception.room;

import org.zipli.socknet.exception.ErrorStatusCodeRoom;

public class GetRoomException extends Throwable {

    private final ErrorStatusCodeRoom errorStatusCodeRoom;

    public GetRoomException(ErrorStatusCodeRoom errorStatusCodeRoom) {
        this.errorStatusCodeRoom = errorStatusCodeRoom;
    }

    public ErrorStatusCodeRoom getErrorStatusCodeRoom() {
        return errorStatusCodeRoom;
    }
}
