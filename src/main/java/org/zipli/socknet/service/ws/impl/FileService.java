package org.zipli.socknet.service.ws.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.Command;
import org.zipli.socknet.dto.FileData;
import org.zipli.socknet.dto.WsMessage;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.FileMessage;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.FileRepository;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.service.ws.IFileService;

import java.io.IOException;
import java.util.Date;

@Service
public class FileService implements IFileService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final FileRepository fileRepository;
    private final MessageService messageService;
    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations operations;

    public FileService(UserRepository userRepository, ChatRepository chatRepository,
                       FileRepository fileRepository, MessageService messageService,
                       GridFsTemplate gridFsTemplate, GridFsOperations operations) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.fileRepository = fileRepository;
        this.messageService = messageService;
        this.gridFsTemplate = gridFsTemplate;
        this.operations = operations;
    }


    @Override
    public FileMessage sendFile(FileData data) throws SendFileException, IOException {
        FileMessage fileMessage;
        Chat chat;
        if (data != null) {
            DBObject metaData = new BasicDBObject();
            metaData.put("type", "file");
            metaData.put("title", data.getTitle());
            ObjectId id = gridFsTemplate.store(
                    data.getMultipartFile().getInputStream(),
                    data.getMultipartFile().getName(),
                    data.getMultipartFile().getContentType(),
                    metaData
            );
            GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
            if (gridFSFile != null) {
                fileMessage = new FileMessage(data.getIdUser(), data.getIdChat(), new Date(),
                        operations.getResource(gridFSFile).getInputStream());

                final FileMessage finalFileMessage = fileRepository.save(fileMessage);
                chat = chatRepository.findChatById(data.getIdChat());
                chat.getIdFiles().add(fileMessage.getId());

                chat.getIdUsers().parallelStream()
                        .forEach(userId -> messageService.sendMessageToUser(userId,
                                new WsMessage(Command.FILE_SEND,
                                        new FileData(userId,
                                                chat.getId(),
                                                finalFileMessage.getId(),
                                                finalFileMessage.getTitle(),
                                                data.getMultipartFile())
                                ))
                        );
            } else {
                throw new SendFileException("GridFSFile is null!");
            }

            chatRepository.save(chat);
            return fileMessage;

        } else {
            throw new IOException("Data is null!");
        }
    }

    @Override
    public void deleteFile(FileData data) throws FileDeleteException, UpdateChatException {
        FileMessage fileMessage = fileRepository.getFileById(data.getFileId());

        if (fileMessage.getAuthorId().equals(data.getIdUser())) {
            Chat chat = chatRepository.findChatById(data.getIdChat());
            if (chat != null) {
//                chat.getIdFiles().remove(fileMessage.getId());
                final Chat finalChat = chatRepository.save(chat);

                finalChat.getIdUsers().parallelStream()
                        .forEach(userId -> messageService.sendMessageToUser(userId,
                                new WsMessage(Command.FILE_DELETE,
                                        new FileData(userId,
                                                finalChat.getId(),
                                                fileMessage.getId(),
                                                fileMessage.getTitle(),
                                                data.getMultipartFile()
                                        )
                                ))
                        );
            } else {
                throw new UpdateChatException("There is no such chat");
            }
            fileRepository.delete(fileMessage);
        } else {
            throw new FileDeleteException("Only the author can delete the file");
        }
    }
}
