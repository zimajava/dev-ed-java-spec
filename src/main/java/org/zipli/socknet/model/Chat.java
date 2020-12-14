package org.zipli.socknet.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
    private List<String> idMessage;
    private List<String> idUsers;
    private String idAdminUser;

    public Chat(String id, String chatName, boolean isPrivate, List<String> idMessage, List<String> idUsers, String idAdminUser) {
        this.id = id;
        this.chatName = chatName;
        this.isPrivate = isPrivate;
        this.idMessage = idMessage;
        this.idUsers = idUsers;
        this.idAdminUser = idAdminUser;
    }
}
