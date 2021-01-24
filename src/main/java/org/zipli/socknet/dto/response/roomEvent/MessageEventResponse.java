package org.zipli.socknet.dto.response.roomEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zipli.socknet.dto.response.roomEvent.RoomEventResponse;

@Getter
@Setter
@NoArgsConstructor
public class MessageEventResponse extends BaseEventResponse {

    private String userName;
    private String textMessage;

    public MessageEventResponse(String roomId, String userName, String textMessage) {
        super(roomId);
        this.userName = userName;
        this.textMessage = textMessage;
    }
}
