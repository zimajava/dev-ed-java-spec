package org.zipli.socknet.service.chat.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.Command;
import org.zipli.socknet.dto.FileData;
import org.zipli.socknet.dto.response.WsMessageResponse;
import org.zipli.socknet.exception.ErrorStatusCode;
import org.zipli.socknet.exception.chat.UpdateChatException;
import org.zipli.socknet.exception.file.FileDeleteException;
import org.zipli.socknet.exception.file.FindFileException;
import org.zipli.socknet.exception.file.SaveFileException;
import org.zipli.socknet.exception.file.SendFileException;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.FileRepository;
import org.zipli.socknet.repository.model.Chat;
import org.zipli.socknet.repository.model.File;
import org.zipli.socknet.service.chat.IFileService;

import java.io.ByteArrayInputStream;
import java.util.Date;

@Slf4j
@Service
public class FileService implements IFileService {

    private final ChatRepository chatRepository;
    private final FileRepository fileRepository;
    private final EmitterService emitterService;
    private final GridFsTemplate gridFsTemplate;

    public FileService(ChatRepository chatRepository, FileRepository fileRepository,
                       EmitterService emitterService, GridFsTemplate gridFsTemplate) {
        this.chatRepository = chatRepository;
        this.fileRepository = fileRepository;
        this.emitterService = emitterService;
        this.gridFsTemplate = gridFsTemplate;
    }

    @Override
    public File sendFile(FileData data) throws SendFileException {
        File file;
        Chat chat;
        final File finalFile;
        try {
            DBObject metaData = new BasicDBObject();
            metaData.put("type", "file");
            metaData.put("title", data.getTitle());
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes());
            ObjectId id = gridFsTemplate.store(
                    inputStream,
                    data.getTitle(),
                    metaData);

            GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
            if (gridFSFile != null) {
                file = new File(data.getUserId(), data.getChatId(), new Date(), data.getTitle(), data.getBytes());
                finalFile = fileRepository.save(file);
                chat = chatRepository.update(data.getChatId(), data.getFileId());
                log.info("Send file to db userId {} chatId {}", data.getUserId(), data.getChatId());

                if (chat != null) {
                    chat.getUsersId().parallelStream()
                            .forEach(userId -> emitterService.sendMessageToUser(userId,
                                    new WsMessageResponse(Command.FILE_SEND,
                                            new FileData(userId,
                                                    chat.getId(),
                                                    finalFile.getId(),
                                                    finalFile.getTitle(),
                                                    finalFile.getBytes())
                                    ))
                            );
                } else {
                    throw new UpdateChatException(ErrorStatusCode.CHAT_NOT_EXISTS);
                }
            } else {
                throw new SaveFileException(ErrorStatusCode.GRID_FS_FILE_IS_NOT_FOUND);
            }
            return finalFile;
        } catch (Exception e) {
            log.error("Error in loading file into DB");
            throw new SendFileException(ErrorStatusCode.FILE_WAS_NOT_LOADING_CORRECT);
        }
    }

    @Override
    public void deleteFile(FileData data) throws FileDeleteException {
        File file = fileRepository.getFileById(data.getFileId());
        Chat chat = chatRepository.findChatById(data.getChatId());
        try {
            if (file != null && file.getAuthorId().equals(data.getUserId()) && chat != null) {
                if (chat.getFilesId().remove(file.getId())) {
                    final Chat finalChat = chatRepository.save(chat);

                    finalChat.getUsersId().parallelStream()
                            .forEach(userId -> emitterService.sendMessageToUser(userId,
                                    new WsMessageResponse(Command.FILE_DELETE,
                                            new FileData(userId,
                                                    finalChat.getId(),
                                                    file.getId(),
                                                    file.getTitle(),
                                                    file.getBytes()
                                            )
                                    ))
                            );
                }
            } else {
                throw new FindFileException(ErrorStatusCode.FILE_IS_NOT_IN_A_DB);
            }
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(data.getFileId())));
            fileRepository.deleteById(file.getId());
            log.info("File is successfully deleted UserId {} ChatId {} FileId {}", data.getUserId(), data.getChatId(), data.getFileId());

        } catch (Exception e) {
            log.error("Error. The given data is invalid");
            throw new FileDeleteException(ErrorStatusCode.FILE_ACCESS_ERROR);
        }
    }
}
