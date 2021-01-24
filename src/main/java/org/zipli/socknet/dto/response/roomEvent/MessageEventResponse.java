package org.zipli.socknet.dto.response.roomEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zipli.socknet.dto.response.roomEvent.RoomEventResponse;

@Getter
@Setter
@NoArgsConstructor
public class MessageEventResponse extends BaseEventResponse {

    private String textMessage;
    public MessageEventResponse(String textMessage) {
        this.textMessage = textMessage;
    }

    public MessageEventResponse(String userName, String textMessage) {
        super(userName);
        this.textMessage = textMessage;
    }

}
