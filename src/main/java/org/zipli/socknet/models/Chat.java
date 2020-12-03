package org.zipli.socknet.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document
@NoArgsConstructor
public class Chat {
    @Id
    private long chatId;

    private String chatName;
    private boolean isPrivate;
    private List<Long> idMessage;

    public Chat(long chatId, boolean isPrivate, List<Long> idMessage) {
        this.chatId = chatId;
        this.isPrivate = isPrivate;
        this.idMessage = idMessage;
    }

    public Chat(long chatId, String chatName, boolean isPrivate, List<Long> idMessage) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.isPrivate = isPrivate;
        this.idMessage = idMessage;
    }
}
