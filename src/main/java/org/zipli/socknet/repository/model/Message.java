package org.zipli.socknet.repository.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id) &&
                Objects.equals(authorId, message.authorId) &&
                Objects.equals(chatId, message.chatId) &&
                Objects.equals(textMessage, message.textMessage) &&
                Objects.equals(date, message.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, authorId, chatId, textMessage, date);
    }
}
