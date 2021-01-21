package org.zipli.socknet.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageEventResponse extends RoomEventResponse {
    private String textMessage;

    public MessageEventResponse(String roomId, String signal, String userName, String textMessage) {
        super(roomId, signal, userName);
        this.textMessage = textMessage;
    }

    public MessageEventResponse(String signal, String userName, String textMessage) {
        super(signal, userName);
        this.textMessage = textMessage;
    }

}
