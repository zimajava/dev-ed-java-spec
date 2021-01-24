package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RoomMessage {

    private String authorUserName;
    private String roomId;
    private String textMessage;
    private long date;

    public RoomMessage(String authorUserName, String roomId, String textMessage, long date) {
        this.authorUserName = authorUserName;
        this.roomId = roomId;
        this.textMessage = textMessage;
        this.date = date;
    }
}
