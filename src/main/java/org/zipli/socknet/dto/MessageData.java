package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.zipli.socknet.repository.model.Message;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MessageData extends ChatData {
    private String messageId;
    private String textMessage;
    private List<Message> messages;

    public MessageData(String idUser, String chatId, String messageId, String textMessage, List<Message> messages) {
        super(idUser, chatId);
        this.messageId = messageId;
        this.textMessage = textMessage;
        this.messages = messages;
    }

    public MessageData(String idUser, String chatId, String messageId, String textMessage) {
        super(idUser, chatId);
        this.messageId = messageId;
        this.textMessage = textMessage;
    }
}
