package org.zipli.socknet.dto.video;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class VideoCallState {
    private String userCreatorId;
    private List<String> usersInCallId;
    private List<String> usersWhoIsNotOnlineId;

    public VideoCallState(String userCreatorId, List<String> usersInCallId, List<String> idUsersWhoNotAnswered) {
        this.userCreatorId = userCreatorId;
        this.usersInCallId = usersInCallId;
        this.usersWhoIsNotOnlineId = idUsersWhoNotAnswered;
    }

    @Override
    public String toString() {
        return "VideoCallState is: idUserCreator='" + userCreatorId + '\'' +
                ", idUsersInCall=" + usersInCallId +
                ", idUsersWhoIsNotOnline=" + usersWhoIsNotOnlineId + '\'';
    }
}
