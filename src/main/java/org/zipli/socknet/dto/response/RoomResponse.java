package org.zipli.socknet.dto.response;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.zipli.socknet.dto.request.UserInfoByRoomRequest;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse {

    @Id
    private String id;
    private String roomName;
    private String creatorUser;
    private List<UserInfoByRoomRequest> users;

}
