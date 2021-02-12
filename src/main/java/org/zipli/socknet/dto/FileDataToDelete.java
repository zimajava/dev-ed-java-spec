package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FileDataToDelete extends ChatData {
    private String fileId;

    public FileDataToDelete(String idUser, String chatId, String fileId) {
        super(idUser, chatId);
        this.fileId = fileId;
    }
}
