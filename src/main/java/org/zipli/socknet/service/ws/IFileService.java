package org.zipli.socknet.service.ws;

import org.zipli.socknet.dto.FileData;
import org.zipli.socknet.exception.file.FindFileException;
import org.zipli.socknet.exception.file.SaveFileException;
import org.zipli.socknet.exception.file.SendFileException;
import org.zipli.socknet.exception.file.FileDeleteException;
import org.zipli.socknet.exception.chat.UpdateChatException;
import org.zipli.socknet.model.File;

public interface IFileService {

    File sendFile(FileData Data) throws SendFileException, UpdateChatException, SaveFileException;

    void deleteFile(FileData data) throws FileDeleteException, UpdateChatException, FindFileException;
}
