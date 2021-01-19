package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChatData extends BaseData {
    private String chatName;
    private List<String> chatParticipants;
    private boolean isPrivate;

    public ChatData(String userId, String chatId, String chatName) {
        super(userId, chatId);
        this.chatName = chatName;
    }

    public ChatData(String userId, String chatName, List<String> chatParticipants, boolean isPrivate) {
        super(userId);
        this.chatName = chatName;
        this.chatParticipants = chatParticipants;
        this.isPrivate = isPrivate;
    }

    public ChatData(String userId, String chatId, String chatName, List<String> chatParticipants, boolean isPrivate) {
        super(userId, chatId);
        this.chatName = chatName;
        this.chatParticipants = chatParticipants;
        this.isPrivate = isPrivate;
    }

    public ChatData(String userId, String chatName, List<String> chatParticipants) {
        super(userId);
        this.chatName = chatName;
        this.chatParticipants = chatParticipants;
    }
}
