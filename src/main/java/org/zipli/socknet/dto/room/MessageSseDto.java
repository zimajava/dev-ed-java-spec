package org.zipli.socknet.dto.room;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageSseDto extends RoomSseDto{
    public MessageSseDto(String roomId, String signal, String userName, String textMessage) {
        super(roomId, signal, userName);
        this.textMessage = textMessage;
    }

    public MessageSseDto(String signal, String userName, String textMessage) {
        super(signal, userName);
        this.textMessage = textMessage;
    }

    private String textMessage;

}
