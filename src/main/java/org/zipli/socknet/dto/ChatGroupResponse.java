package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatGroupResponse {

    private String idChat;
    private String idUser;
    private String chatName;
    private String keyRoom;

    public ChatGroupResponse(String idChat, String idUser, String chatName) {
        this.idChat = idChat;
        this.idUser = idUser;
        this.chatName = chatName;
        this.keyRoom = "false";
    }
}
