package models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class Message {
    @Id
    private long id;

    private String inputChat;
    private String outputChat;
    private String message;

    public Message(long id, String to, String outputChat, String message) {
        this.id = id;
        this.inputChat = to;
        this.outputChat = outputChat;
        this.message = message;
    }
}
