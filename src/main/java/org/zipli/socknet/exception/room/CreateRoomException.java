package org.zipli.socknet.exception.room;

import org.zipli.socknet.exception.ErrorStatusCodeRoom;

public class CreateRoomException extends Throwable {

    private final ErrorStatusCodeRoom errorStatusCodeRoom;

    public CreateRoomException(ErrorStatusCodeRoom errorStatusCodeRoom) {
        this.errorStatusCodeRoom = errorStatusCodeRoom;
    }

    public ErrorStatusCodeRoom getErrorStatusCodeRoom() {
        return errorStatusCodeRoom;
    }
}
