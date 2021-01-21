package org.zipli.socknet.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zipli.socknet.repository.model.User;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FullUserInfo extends UserInfo{
    private List<String> chatsId;

    public FullUserInfo(User user) {
        super(user.getEmail(), user.getId(),user.getUserName(), user.getNickName(), user.getAvatar());
        this.chatsId = user.getChatsId();
    }

    public FullUserInfo(List<String> chatsId) {
        this.chatsId = chatsId;
    }

}
