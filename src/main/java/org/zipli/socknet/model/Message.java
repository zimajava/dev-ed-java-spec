package org.zipli.socknet.model;

import com.sun.mail.iap.ByteArray;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Document
@NoArgsConstructor
public class Message {

    @Id
    private String id;

    private String authorId;
    private String chatId;
    private String textMessage;
    private ByteArray message;

    public Message(String authorId, String chatId, String textMessage) {
        this.authorId = authorId;
        this.chatId = chatId;
        this.textMessage = textMessage;
    }

    public Message(String authorId, String chatId, ByteArray message) {
        this.authorId = authorId;
        this.chatId = chatId;
        this.message = message;
    }
}
