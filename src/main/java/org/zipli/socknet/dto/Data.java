package org.zipli.socknet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Data {

    private String textMessage;
    private String userId;
    private String chatId;
    private String messageId;
    private String userName;
    private String nameChat;

    public Data(String textMessage, String userId, String chatId, String userName, String nameChat) {
        this.textMessage = textMessage;
        this.userId = userId;
        this.chatId = chatId;
        this.userName = userName;
        this.nameChat = nameChat;
    }

    public Data(String chatId, String nameChat) {
        this.chatId = chatId;
        this.nameChat = nameChat;
    }
}
