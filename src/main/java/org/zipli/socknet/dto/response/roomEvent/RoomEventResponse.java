package org.zipli.socknet.dto.response.roomEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoomEventResponse extends BaseEventResponse {

    private String signal;

    public RoomEventResponse(String signal) {
        this.signal = signal;
    }

    public RoomEventResponse(String userName, String signal) {
        super(userName);
        this.signal = signal;
    }
}
