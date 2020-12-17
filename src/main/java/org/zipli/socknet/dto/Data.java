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
    private String userName;
    private String nameChat;

    public Data(String chatId, String nameChat) {
        this.chatId = chatId;
        this.nameChat = nameChat;
    }
}
