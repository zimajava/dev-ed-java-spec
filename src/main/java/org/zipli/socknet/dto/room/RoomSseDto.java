package org.zipli.socknet.dto.room;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoomSseDto extends BaseSseDto{

    private String signal;

    private String userName;

    public RoomSseDto(String roomId, String signal, String userName) {
        super(roomId);
        this.signal = signal;
        this.userName = userName;
    }

    public RoomSseDto(String signal, String userName) {
        this.signal = signal;
        this.userName = userName;
    }
}
