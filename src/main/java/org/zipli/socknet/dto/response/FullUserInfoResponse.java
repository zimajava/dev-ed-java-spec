package org.zipli.socknet.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zipli.socknet.repository.model.User;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FullUserInfoResponse extends UserInfoResponse {
    private List<String> chatsId;
    private String userId;

    public FullUserInfoResponse(User user) {
        super(user.getEmail(), user.getUserName(), user.getNickName(), user.getAvatar());
        this.userId = user.getId();
        this.chatsId = user.getChatsId();
    }

    public FullUserInfoResponse(List<String> chatsId) {
        this.chatsId = chatsId;
    }

}
