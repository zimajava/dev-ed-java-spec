package org.zipli.socknet.model;

import lombok.Setter;
import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private String creatorUserId;

    public Chat(String chatName, boolean isPrivate, List<String> idMessages, List<String> idUsers, String creatorUserId) {
        this.chatName = chatName;
        this.isPrivate = isPrivate;
        this.idMessages = idMessages;
        this.idUsers = idUsers;
        this.creatorUserId = creatorUserId;
    }
}
