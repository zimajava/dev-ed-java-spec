package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatGroupData extends BaseData {
    private String chatName;
    private List<String> groupUsersIds;
    private boolean isRoom;

    public ChatGroupData(String idUser, String idChat, String chatName, List<String> secondUserId, boolean isRoom) {
        super(idUser, idChat);
        this.chatName = chatName;
        this.groupUsersIds = secondUserId;
        this.isRoom = isRoom;
    }

    public ChatGroupData(String idUser, String idChat, String chatName, List<String> secondUserId) {
        super(idUser, idChat);
        this.chatName = chatName;
        this.groupUsersIds = secondUserId;
    }
}
