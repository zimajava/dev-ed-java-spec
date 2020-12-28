package org.zipli.socknet.payload.request;

import com.sun.mail.iap.ByteArray;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;



@Getter
@Setter
public class MyAccountChange extends SignupRequest {
    @NotBlank(message = "UserId can't be empty")
    @NotNull
    private String userId;
    private ByteArray avatar;

    public MyAccountChange(String email, String password, String userName, String nickName, ByteArray avatar, String userId) {
        super(email, password, userName, nickName);
        this.avatar = avatar;
        this.userId = userId;
    }
}
