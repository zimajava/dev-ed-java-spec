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
public class File {

    @Id
    private String id;

    private String authorId;
    private String chatId;
    private String title;
    private byte[] bytes;
    private Date date;

    public File(String authorId, String chatId, Date date, String title) {
        this.authorId = authorId;
        this.chatId = chatId;
        this.date = date;
        this.title = title;
    }

    public File(String authorId, String chatId, Date date, String title, byte[] bytes) {
        this.authorId = authorId;
        this.chatId = chatId;
        this.date = date;
        this.title = title;
        this.bytes = bytes;
    }
}
