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

    public ChatData(String userId, String idChat, String chatName, String secondUserId) {
        super(userId, idChat);
        this.chatName = chatName;
        this.secondUserId = secondUserId;
    }

    public ChatData(String userId, String idChat, String chatName) {
        super(userId, idChat);
        this.chatName = chatName;
    }

    public ChatData(String idChat, String chatName) {
        super(null, idChat);
        this.chatName = chatName;
    }

}
