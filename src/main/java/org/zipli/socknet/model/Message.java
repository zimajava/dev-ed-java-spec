package org.zipli.socknet.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

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
    private Date date;

    public Message(String authorId, String chatId, Date date, String textMessage) {
        this.authorId = authorId;
        this.chatId = chatId;
        this.date = date;
        this.textMessage = textMessage;
    }

}
