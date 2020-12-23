package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.Setter;
import org.zipli.socknet.model.Chat;

import java.util.List;

@Getter
@Setter
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
//
//    public DataChat(String chatName) {
//        super(chatId);
//        this.chatName = chatName;
//        this.chatId = chatId;
//    }

    public DataChat(String id, String chatName) {
//        this.id = id;
        this.chatName = chatName;
    }

    public String getChatName() {
        return chatName;
    }

    public List<Chat> getChats() {
        return chats;
    }

    @Override
    public String toString() {
        return "DataChat{" +
                ", chatName='" + chatName + '\'' +
                ", chats=" + chats +
                '}';
    }
}
