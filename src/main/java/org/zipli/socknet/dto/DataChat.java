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
public class DataChat extends DataBase{
    private String chatName;
    private String secondUserId;
//    private String id;
    List<Chat> chats;

    public DataChat(String userId, String idChat, String chatName, List<Chat> chats, String secondUserId) {
        super(userId, idChat);
        this.chatName = chatName;
        this.chats = chats;
        this.secondUserId = secondUserId;
    }

    public DataChat(String id, String chatName) {
//        this.id = id;
        this.chatName = chatName;
    }
    public DataChat(String userId, String idChat, String chatName) {
        super(userId, idChat);
        this.chatName = chatName;
        this.chats = chats;
        this.secondUserId = secondUserId;
    }
}
