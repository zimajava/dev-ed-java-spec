package org.zipli.socknet.model;

import lombok.Setter;
import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@ToString
@Document
@NoArgsConstructor
public class Chat {

    @Id
    private String id;
    private String chatName;
    private boolean isPrivate;
    private List<Long> idMessage;

    public Chat(boolean isPrivate, List<Long> idMessage) {
        this.isPrivate = isPrivate;
        this.idMessage = idMessage;
    }

    public Chat(String chatName, boolean isPrivate, List<Long> idMessage) {
        this.chatName = chatName;
        this.isPrivate = isPrivate;
        this.idMessage = idMessage;
    }
}
