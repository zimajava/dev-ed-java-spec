package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.zipli.socknet.model.Chat;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChatData extends BaseData {
    private String chatName;
    private String secondUserId;
    private List<Chat> chats;

    public ChatData(String userId, String idChat, String chatName, List<Chat> chats, String secondUserId) {
        super(userId, idChat);
        this.chatName = chatName;
        this.chats = chats;
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

    public ChatData(List<Chat> chats) {
        this.chats = chats;
    }
}
