package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChatData extends BaseData {
    private String chatName;
    private String secondUserId;
    private boolean isRoom;

    public ChatData(String userId, String idChat, String chatName, String secondUserId, boolean isRoom) {
        super(userId, idChat);
        this.chatName = chatName;
        this.secondUserId = secondUserId;
        this.isRoom = isRoom;
    }

    public ChatData(String userId, String idChat, String chatName, boolean isRoom) {
        super(userId, idChat);
        this.chatName = chatName;
        this.isRoom = isRoom;
    }

    public ChatData(String idChat, String chatName, boolean isRoom) {
        super(null, idChat);
        this.chatName = chatName;
        this.isRoom = isRoom;
    }

}
