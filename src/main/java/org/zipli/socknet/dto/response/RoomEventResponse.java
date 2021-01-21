package org.zipli.socknet.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoomEventResponse extends BaseEventResponse {

    private String signal;

    private String userName;

    public RoomEventResponse(String roomId, String signal, String userName) {
        super(roomId);
        this.signal = signal;
        this.userName = userName;
    }

    public RoomEventResponse(String signal, String userName) {
        this.signal = signal;
        this.userName = userName;
    }
}
