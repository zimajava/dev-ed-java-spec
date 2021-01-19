package org.zipli.socknet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zipli.socknet.model.User;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String userId;
    private String userName;
    private String avatar;

    public UserResponse(User user) {
        this.userId = user.getId();
        this.userName= user.getUserName();
        if (user.getAvatar()==null){
            this.avatar="";
        }else {
            this.avatar=user.getAvatar();
        }
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
