package org.zipli.socknet.service.ws.impl;

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
import org.zipli.socknet.dto.WsMessageResponse;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.exception.chat.UpdateChatException;
import org.zipli.socknet.exception.file.FileDeleteException;
import org.zipli.socknet.exception.file.FindFileException;
import org.zipli.socknet.exception.file.SaveFileException;
import org.zipli.socknet.exception.file.SendFileException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.File;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.FileRepository;
import org.zipli.socknet.service.ws.IFileService;

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
    public File sendFile(FileData data) throws SendFileException, UpdateChatException, SaveFileException {
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
                file = new File(data.getIdUser(), data.getIdChat(), new Date(), data.getTitle(), data.getBytes());
                finalFile = fileRepository.save(file);
                chat = chatRepository.findChatById(data.getIdChat());

                if (chat != null) {
                    chat.getIdFiles().add(file.getId());

                    chat.getIdUsers().parallelStream()
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
                    throw new UpdateChatException("Chat doesn't exist", WsException.CHAT_NOT_EXISTS);
                }
            } else {
                throw new SaveFileException("GridFSFile is null!", WsException.GRIDFSFILE_IS_NOT_FOUND);
            }
            chatRepository.save(chat);
            return finalFile;
        } catch (Exception e) {
            log.error("Error in loading file into DB");
            throw new SendFileException("Error in loading file into DB", WsException.FILE_WAS_NOT_LOADING_CORRECT);
        }
    }

    @Override
    public void deleteFile(FileData data) throws FileDeleteException, UpdateChatException, FindFileException {
        File file = fileRepository.getFileById(data.getFileId());

        if (file != null && file.getAuthorId().equals(data.getIdUser())) {
            Chat chat = chatRepository.findChatById(data.getIdChat());
            if (chat != null) {
                if (chat.getIdFiles().remove(file.getId())) {
                    final Chat finalChat = chatRepository.save(chat);

                    finalChat.getIdUsers().parallelStream()
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
                } else {
                throw new FindFileException("This file does not exists", WsException.FILE_IS_NOT_IN_A_DB);
                }
            } else {
                throw new UpdateChatException("There is no such chat", WsException.CHAT_NOT_EXISTS);
            }
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(data.getFileId())));
            fileRepository.deleteById(file.getId());
        } else {
            throw new FileDeleteException("Only the author can delete the file", WsException.FILE_ACCESS_ERROR);
        }
    }
}
