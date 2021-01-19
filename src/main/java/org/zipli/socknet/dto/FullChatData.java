package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FullChatData extends ChatData {
    private String chatId;


    public FullChatData(String userId, String chatName, List<String> chatParticipants, boolean isPrivate, String chatId) {
        super(userId, chatName, chatParticipants, isPrivate);
        this.chatId = chatId;
    }
}
