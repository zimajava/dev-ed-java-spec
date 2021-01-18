package org.zipli.socknet.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.zipli.socknet.dto.UserInfoByRoom;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Document
@NoArgsConstructor
public class Room{

    @Id
    private String id;
    private String chatName;
    private List<String> idMessages = new ArrayList<>();
    private List<UserInfoByRoom> users = new ArrayList<>();
    private List<String> signals = new ArrayList<>();
    private String creatorUserId;

    public Room(String chatName, String creatorUserId) {
        this.chatName = chatName;
        this.idMessages = new ArrayList<>();
        this.users = new ArrayList<>();
        this.signals = new ArrayList<>();
        this.creatorUserId = creatorUserId;
    }

}
