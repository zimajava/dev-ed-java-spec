package org.zipli.socknet.dto.video;

public class VideoCallState {
    private String idUserCreator;
    private String idUsersWhoAnswered;
    private String idUsersWhoNotAnswered;

    public VideoCallState(String idUserCreator, String idUsersWhoAnswered, String idUsersWhoNotAnswered) {
        this.idUserCreator = idUserCreator;
        this.idUsersWhoAnswered = idUsersWhoAnswered;
        this.idUsersWhoNotAnswered = idUsersWhoNotAnswered;
    }
}
