package org.zipli.socknet.repository.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.zipli.socknet.dto.RoomMessage;
import org.zipli.socknet.dto.request.UserInfoByRoomRequest;

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
    private String roomName;
    private String creatorUserName;
    private List<UserInfoByRoomRequest> usersInfo;
    private List<RoomMessage> messages;

    public Room(String roomName, String creatorUserName) {
        this.roomName = roomName;
        this.usersInfo = new ArrayList<>();
        this.creatorUserName = creatorUserName;
        this.messages = new ArrayList<>();
    }
}
