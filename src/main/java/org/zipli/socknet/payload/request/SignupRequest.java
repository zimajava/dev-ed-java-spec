package org.zipli.socknet.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

    private String email;
    private String password;
    private String userName;
    private String nickName;

    public SignupRequest(String email, String password, String userName, String nickName) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.nickName = nickName;
    }
}
