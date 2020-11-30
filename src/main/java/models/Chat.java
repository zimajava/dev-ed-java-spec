package models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class Chat {
    @Id
    private long id;

    private String chatName;
    private boolean isPrivate;
    private String message;

    public Chat(long id, String chatName, boolean isPrivate, String message) {
        this.id = id;
        this.chatName = chatName;
        this.isPrivate = isPrivate;
        this.message = message;
    }
}
