package org.zipli.socknet.exception.room;

import org.zipli.socknet.dto.EventCommandSse;
import org.zipli.socknet.exception.ErrorStatusCodeRoom;

public class LiveRoomException extends Throwable {

    private final ErrorStatusCodeRoom errorStatusCodeRoom;

    public LiveRoomException(String message,ErrorStatusCodeRoom errorStatusCodeRoom) {
        super(message);
        this.errorStatusCodeRoom = errorStatusCodeRoom;
    }

    public ErrorStatusCodeRoom getErrorStatusCodeRoom() {
        return errorStatusCodeRoom;
    }
}
