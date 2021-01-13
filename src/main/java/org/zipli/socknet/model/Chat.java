package org.zipli.socknet.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Document
@NoArgsConstructor
public class Chat {

    @Id
    private String id;
    private String chatName;
    private boolean isPrivate;
    private boolean isRoom;
    private List<String> idMessages;
    private List<String> idUsers;
    private String creatorUserId;

    public Chat(String chatName, boolean isPrivate, boolean isRoom, List<String> idMessages, List<String> idUsers, String creatorUserId) {
        this.chatName = chatName;
        this.isPrivate = isPrivate;
        this.isRoom = isRoom;
        this.idMessages = idMessages;
        this.idUsers = idUsers;
        this.creatorUserId = creatorUserId;
    }

    public Chat(String chatName, boolean isPrivate, boolean isRoom, String creatorUserId) {
        this.chatName = chatName;
        this.isPrivate = isPrivate;
        this.isRoom = isRoom;
        this.idMessages = new ArrayList<>();
        this.idUsers = new ArrayList<>();
        this.creatorUserId = creatorUserId;
    }
}
