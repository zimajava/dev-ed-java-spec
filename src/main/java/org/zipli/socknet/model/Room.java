package org.zipli.socknet.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.zipli.socknet.dto.MessageRoom;
import org.zipli.socknet.dto.UserInfoByRoom;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Document
@NoArgsConstructor
public class Room {

    @Id
    private String id;
    private String chatName;
    private String creatorUser;
    private List<UserInfoByRoom> users;
    private List<MessageRoom> messages;

    public Room(String chatName, String creatorUser) {
        this.chatName = chatName;
        this.users = new ArrayList<>();
        this.creatorUser = creatorUser;
        this.messages = new ArrayList<>();
    }
}
