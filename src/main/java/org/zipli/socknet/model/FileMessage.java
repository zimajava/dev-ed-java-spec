package org.zipli.socknet.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.InputStream;
import java.util.Date;

@Getter
@Setter
@ToString
@Document
@NoArgsConstructor
public class FileMessage {

    @Id
    private String id;

    private String authorId;
    private String chatId;
    private String title;
    private InputStream stream;
    private Date date;

    public FileMessage(String authorId, String chatId, Date date, String title) {
        this.authorId = authorId;
        this.chatId = chatId;
        this.date = date;
        this.title = title;
    }

    public FileMessage(String authorId, String chatId, Date date, InputStream stream) {
        this.authorId = authorId;
        this.chatId = chatId;
        this.date = date;
        this.stream = stream;
    }
}
