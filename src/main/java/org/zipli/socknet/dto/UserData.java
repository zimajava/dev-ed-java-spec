package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserData {
    private String userId;
    private String accessToken;
    private String refreshToken;

    public UserData(String userId, String accessToken, String refreshToken) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
