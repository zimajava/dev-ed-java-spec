package org.zipli.socknet.exception.room;

import org.zipli.socknet.exception.ErrorStatusCode;

public class CreateRoomException extends Throwable {

    private final ErrorStatusCode errorStatusCodeRoom;

    public CreateRoomException(ErrorStatusCode errorStatusCodeRoom) {
        this.errorStatusCodeRoom = errorStatusCodeRoom;
    }

    public ErrorStatusCode getErrorStatusCodeRoom() {
        return errorStatusCodeRoom;
    }
}
