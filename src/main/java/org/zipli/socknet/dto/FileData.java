package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FileData extends ChatData {
    private String fileId;

    private String title;
    private byte[] bytes;

    public FileData(String userId, String chatId, String fileId, String title, byte[] bytes) {
        super(userId, chatId);
        this.fileId = fileId;
        this.title = title;
        this.bytes = bytes;
    }

    public FileData(String userId, String chatId, String title, byte[] finalBytes) {
        super(userId, chatId);
        this.title = title;
        this.bytes = finalBytes;
    }
}
