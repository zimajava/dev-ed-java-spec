package org.zipli.socknet.service.ws;

import org.zipli.socknet.dto.FileData;
import org.zipli.socknet.exception.SendFileException;
import org.zipli.socknet.exception.FileDeleteException;
import org.zipli.socknet.exception.UpdateChatException;
import org.zipli.socknet.model.FileMessage;

import java.io.IOException;

public interface IFileService {

    FileMessage sendFile(FileData Data) throws SendFileException, IOException;

    void deleteFile(FileData data) throws FileDeleteException, UpdateChatException;
}
