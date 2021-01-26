package org.zipli.socknet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zipli.socknet.repository.model.User;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private String email;
    private String userId;
    private String userName;
    private String nickName;
    private String avatar;

    public UserInfoResponse(User user) {
        this.email = user.getEmail();
        this.userName = user.getUserName();
        this.nickName = user.getNickName();
        if (user.getAvatar() == null) {
            this.avatar = "";
        } else {
            this.avatar = user.getAvatar();
        }
    }
}
