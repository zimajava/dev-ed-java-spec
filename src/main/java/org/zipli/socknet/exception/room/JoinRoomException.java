package org.zipli.socknet.exception.room;

import org.zipli.socknet.exception.ErrorStatusCodeRoom;

public class JoinRoomException extends Throwable {

    private final ErrorStatusCodeRoom errorStatusCodeRoom;

    public JoinRoomException(ErrorStatusCodeRoom errorStatusCodeRoom) {
        this.errorStatusCodeRoom = errorStatusCodeRoom;
    }

    public ErrorStatusCodeRoom getErrorStatusCodeRoom() {
        return errorStatusCodeRoom;
    }
}
