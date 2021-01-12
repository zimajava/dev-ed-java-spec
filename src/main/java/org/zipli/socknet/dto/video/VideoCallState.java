package org.zipli.socknet.dto.video;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class VideoCallState {
    private String idUserCreator;
    private List<String> idUsersInCall;
    private List<String> idUsersWhoIsNotOnline;

    public VideoCallState(String idUserCreator, List<String> idUsersInCall, List<String> idUsersWhoNotAnswered) {
        this.idUserCreator = idUserCreator;
        this.idUsersInCall = idUsersInCall;
        this.idUsersWhoIsNotOnline = idUsersWhoNotAnswered;
    }

    @Override
    public String toString() {
        return "VideoCallState is: idUserCreator='" + idUserCreator + '\'' +
                ", idUsersInCall=" + idUsersInCall +
                ", idUsersWhoIsNotOnline=" + idUsersWhoIsNotOnline + '\'';
    }
}
