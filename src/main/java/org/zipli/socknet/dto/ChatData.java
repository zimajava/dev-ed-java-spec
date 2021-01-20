package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatData extends BaseData {
    private String chatId;

    public ChatData(String userId) {
        super(userId);
    }

    public ChatData(String userId, String chatId) {
        super(userId);
        this.chatId = chatId;
    }
}
