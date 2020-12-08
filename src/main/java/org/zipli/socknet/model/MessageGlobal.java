package org.zipli.socknet.model;

import lombok.Setter;
import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import com.sun.mail.iap.ByteArray;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Document
@NoArgsConstructor
public class MessageGlobal {

    @Id
    private String id;

    private String authorId;
    private String chatId;
    private String textMessage;
    private ByteArray message;

    public MessageGlobal(String authorId, String chatId, String textMessage) {
        this.authorId = authorId;
        this.chatId = chatId;
        this.textMessage = textMessage;
    }

    public MessageGlobal(String authorId, String chatId, ByteArray message) {
        this.authorId = authorId;
        this.chatId = chatId;
        this.message = message;
    }
}
