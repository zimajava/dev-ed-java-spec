package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
public class RoomMessage {

    private String authorUserName;
    private String roomId;
    private String textMessage;
    private Date date;

    public RoomMessage(String authorUserName, String roomId, String textMessage, Date date) {
        this.authorUserName = authorUserName;
        this.roomId = roomId;
        this.textMessage = textMessage;
        this.date = date;
    }
}
