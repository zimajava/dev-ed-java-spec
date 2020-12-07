package org.zipli.socknet.models;

import com.sun.mail.iap.ByteArray;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
@NoArgsConstructor
public class Message {
    @Id
    private long messageId;

    private long authorId;
    private long chatId;
    private String textMessage;
    private ByteArray message;

    public Message(long messageId, long authorId, long chatId, String textMessage) {
        this.messageId = messageId;
        this.authorId = authorId;
        this.chatId = chatId;
        this.textMessage = textMessage;
    }

    public Message(long messageId, long authorId, long chatId, ByteArray message) {
        this.messageId = messageId;
        this.authorId = authorId;
        this.chatId = chatId;
        this.message = message;
    }
}
