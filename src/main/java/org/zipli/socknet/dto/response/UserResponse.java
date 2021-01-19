package org.zipli.socknet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zipli.socknet.model.User;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String email;
    private String userName;
    private String nickName;
    private List<String> chatsId;
    private String avatar;

    public UserResponse(User user) {
        this.email = user.getEmail();
        this.userName = user.getUserName();
        this.nickName = user.getNickName();
        this.chatsId = user.getChatsId();
        if (user.getAvatar() == null) {
            this.avatar = "";
        } else {
            this.avatar = user.getAvatar();
        }
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                ", userName='" + userName + '\'' +
                '}';
    }
}
