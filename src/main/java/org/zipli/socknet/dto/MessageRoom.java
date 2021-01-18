package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
public class MessageRoom {

    private String authorUserName;
    private String roomId;
    private String textMessage;
    private Date date;

    public MessageRoom(String authorUserName, String roomId, String textMessage, Date date) {
        this.authorUserName = authorUserName;
        this.roomId = roomId;
        this.textMessage = textMessage;
        this.date = date;
    }
}
