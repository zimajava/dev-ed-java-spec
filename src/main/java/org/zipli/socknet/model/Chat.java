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
    private List<String> messagesId;
    private List<String> usersId;
    private List<String> filesId;
    private String creatorUserId;

    public Chat(String chatName, boolean isPrivate, List<String> usersId, String creatorUserId) {
        this.chatName = chatName;
        this.isPrivate = isPrivate;
        this.messagesId = new ArrayList<>();
        this.usersId = usersId;
        this.creatorUserId = creatorUserId;
    }

    public Chat(String chatName, boolean isPrivate, String creatorUserId) {
        this.chatName = chatName;
        this.isPrivate = isPrivate;
        this.messagesId = new ArrayList<>();
        this.usersId = new ArrayList<>();
        this.filesId = new ArrayList<>();
        this.creatorUserId = creatorUserId;
    }

    public Chat(String chatName, boolean isPrivate, List<String> usersId, String userId, List<String> fileIds) {
        this.chatName = chatName;
        this.isPrivate = isPrivate;
        this.usersId = usersId;
        this.creatorUserId = userId;
        this.filesId = fileIds;
        this.messagesId = new ArrayList<>();
    }
}
