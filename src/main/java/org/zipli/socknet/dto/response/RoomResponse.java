package org.zipli.socknet.dto.response;

import lombok.*;
import org.zipli.socknet.dto.request.UserInfoByRoomRequest;
import org.zipli.socknet.repository.model.Room;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse {

    private String id;
    private String roomName;
    private String creatorUser;
    private List<UserInfoByRoomRequest> users;

    public RoomResponse(Room room) {
        this.id=room.getId();
        this.roomName=room.getRoomName();
        this.creatorUser = room.getCreatorUserName();
        this.users = room.getUsersInfo();
    }
}
