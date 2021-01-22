package org.zipli.socknet.exception.room;

import org.zipli.socknet.exception.ErrorStatusCode;

public class SendMessageToRoomException extends Throwable {

    private final ErrorStatusCode errorStatusCodeRoom;

    public SendMessageToRoomException(ErrorStatusCode errorStatusCodeRoom) {
        this.errorStatusCodeRoom = errorStatusCodeRoom;
    }

    public ErrorStatusCode getErrorStatusCodeRoom() {
        return errorStatusCodeRoom;
    }
}