package org.zipli.socknet.service.ws;

import org.zipli.socknet.dto.FileData;
import org.zipli.socknet.exception.SendFileException;
import org.zipli.socknet.exception.FileDeleteException;
import org.zipli.socknet.exception.chat.UpdateChatException;
import org.zipli.socknet.model.File;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface IFileService {

    File sendFile(FileData Data) throws SendFileException, IOException;

    void deleteFile(FileData data) throws FileDeleteException, UpdateChatException, FileNotFoundException;
}
