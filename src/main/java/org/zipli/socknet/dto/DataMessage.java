package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.zipli.socknet.model.Message;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DataMessage extends DataBase {
    private String messageId;
    private String messageName;
    private String textMessage;
    List<Message> messages;

    public DataMessage(String id, String userName, String messageId, String messageName, String textMessage, List<Message> messages) {
        super(id, userName);
        this.messageId = messageId;
        this.messageName = messageName;
        this.textMessage = textMessage;
        this.messages = messages;
    }
}
