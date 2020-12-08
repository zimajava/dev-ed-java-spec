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
public class MessagePrivate {

    @Id
    private String id;

    private String authorId;
    private String hostUserId;
    private String textMessage;
    private ByteArray message;

    public MessagePrivate(String authorId, String hostUserId, String textMessage) {
        this.authorId = authorId;
        this.hostUserId = hostUserId;
        this.textMessage = textMessage;
    }

    public MessagePrivate(String authorId, String hostUserId, ByteArray message) {
        this.authorId = authorId;
        this.hostUserId = hostUserId;
        this.message = message;
    }
}
