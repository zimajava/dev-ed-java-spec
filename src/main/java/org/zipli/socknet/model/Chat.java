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
    private List<String> idMessages;
    private List<String> idUsers;
    private List<String> idFiles;
    private String creatorUserId;

    public Chat(String chatName, boolean isPrivate, List<String> idUsers, String creatorUserId) {
        this.chatName = chatName;
        this.isPrivate = isPrivate;
        this.idMessages = new ArrayList<>();
        this.idUsers = idUsers;
        this.creatorUserId = creatorUserId;
    }

    public Chat(String chatName, boolean isPrivate, String creatorUserId) {
        this.chatName = chatName;
        this.isPrivate = isPrivate;
        this.idMessages = new ArrayList<>();
        this.idUsers = new ArrayList<>();
        this.idFiles = new ArrayList<>();
        this.creatorUserId = creatorUserId;
    }

    public Chat(String chatName, boolean isPrivate, List<String> idUsers, String userId, List<String> fileIds) {
        this.chatName = chatName;
        this.isPrivate = isPrivate;
        this.idUsers = idUsers;
        this.creatorUserId = userId;
        this.idFiles = fileIds;
        this.idMessages = new ArrayList<>();
    }
}
