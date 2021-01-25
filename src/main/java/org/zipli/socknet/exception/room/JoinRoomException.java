package org.zipli.socknet.exception.room;

import org.zipli.socknet.exception.ErrorStatusCode;

public class JoinRoomException extends Throwable {

    private final ErrorStatusCode errorStatusCodeRoom;

    public JoinRoomException(ErrorStatusCode errorStatusCodeRoom) {
        this.errorStatusCodeRoom = errorStatusCodeRoom;
    }

    public ErrorStatusCode getErrorStatusCodeRoom() {
        return errorStatusCodeRoom;
    }
}
