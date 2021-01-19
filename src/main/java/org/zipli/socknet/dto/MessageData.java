package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.zipli.socknet.model.Message;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MessageData extends BaseData {
    private String messageId;
    private String textMessage;
    private Date timestamp;
    private List<Message> messages;


    public MessageData(List<Message> messages) {
        this.messages = messages;
    }

    public MessageData(String userId, String chatId, String textMessage, Date timestamp) {
        super(userId, chatId);
        this.textMessage = textMessage;
        this.timestamp = timestamp;
    }

    public MessageData(String userId, String chatId, String messageId, String textMessage, Date timestamp) {
        super(userId, chatId);
        this.messageId = messageId;
        this.textMessage = textMessage;
        this.timestamp = timestamp;
    }



}
