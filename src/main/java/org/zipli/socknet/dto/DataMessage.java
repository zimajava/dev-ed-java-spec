package org.zipli.socknet.dto;

import org.zipli.socknet.model.Message;

import java.util.List;

public class DataMessage extends DataBase{
    private String messageId;
    private String messageName;
    List<Message> messages;

    public DataMessage(String id, String userName, String messageId, String messageName, List <Message> messages) {
        super(id, userName);
        this.messageId = messageId;
        this.messageName = messageName;
        this.messages = messages;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessageName() {
        return messageName;
    }

    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return "DataMessage{" +
                "messageId='" + messageId + '\'' +
                ", messageName='" + messageName + '\'' +
                ", messages=" + messages +
                '}';
    }
}
