package org.zipli.socknet.service.chat;

import org.zipli.socknet.dto.FileData;
import org.zipli.socknet.exception.file.FileDeleteException;
import org.zipli.socknet.exception.file.SendFileException;
import org.zipli.socknet.repository.model.File;

public interface IFileService {

    File sendFile(FileData Data) throws SendFileException;

    void deleteFile(FileData data) throws FileDeleteException;
}
