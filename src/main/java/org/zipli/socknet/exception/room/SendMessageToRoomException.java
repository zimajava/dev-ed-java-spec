package org.zipli.socknet.exception.room;

import org.zipli.socknet.exception.ErrorStatusCodeRoom;

public class SendMessageToRoomException extends Throwable {

    private final ErrorStatusCodeRoom errorStatusCodeRoom;

    public SendMessageToRoomException(ErrorStatusCodeRoom errorStatusCodeRoom) {
        this.errorStatusCodeRoom = errorStatusCodeRoom;
    }

    public ErrorStatusCodeRoom getErrorStatusCodeRoom() {
        return errorStatusCodeRoom;
    }
}