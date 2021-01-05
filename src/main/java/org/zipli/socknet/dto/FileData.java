package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;
import org.zipli.socknet.model.File;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FileData extends BaseData {
    private String fileId;

    private String title;
    private MultipartFile multipartFile;
    private List<File> files;

    public FileData(String idUser, String chatId, String fileId, String title) {
        super(idUser, chatId);
        this.fileId = fileId;
        this.title = title;
    }

    public FileData(List <File> files){
        this.files = files;
    }

    public FileData(String idUser, String chatId, String fileId, String title, MultipartFile multipartFile) {
        super(idUser, chatId);
        this.fileId = fileId;
        this.title = title;
        this.multipartFile = multipartFile;
    }
}
