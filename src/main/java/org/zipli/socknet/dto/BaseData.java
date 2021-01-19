package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaseData extends UserData {
    private String chatId;

    public BaseData(String userId) {
        super(userId);
    }

    public BaseData(String userId, String chatId) {
        super(userId);
        this.chatId = chatId;
    }
}
