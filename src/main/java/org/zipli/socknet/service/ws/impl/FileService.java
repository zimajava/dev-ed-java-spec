package org.zipli.socknet.service.ws.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zipli.socknet.dto.Command;
import org.zipli.socknet.dto.FileData;
import org.zipli.socknet.dto.MessageData;
import org.zipli.socknet.dto.WsMessage;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.File;
import org.zipli.socknet.model.Message;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.FileRepository;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.service.ws.IFileService;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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
    public File sendFile(FileData data) throws SendFileException, IOException {

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
            File file = new File(data.getIdUser(), data.getIdChat(), new Date(),
                    operations.getResource(gridFSFile).getInputStream());

            final File finalFile = fileRepository.save(file);
            Chat chat = chatRepository.findChatById(data.getIdChat());
            chat.getIdFiles().add(file.getId());

            chat.getIdUsers().parallelStream()
                    .forEach(userId -> messageService.sendMessageToUser(userId,
                            new WsMessage(Command.FILE_SEND,
                                    new FileData(userId,
                                            chat.getId(),
                                            finalFile.getId(),
                                            finalFile.getTitle(),
                                            data.getMultipartFile())
                            ))
                    );

            chatRepository.save(chat);
            return file;
        } else {
            throw new SendFileException("GridFSFile is null!");
        }
    }

    @Override
    public void deleteFile(FileData data) throws FileDeleteException, UpdateChatException {
        File file = fileRepository.getFileById(data.getFileId());

        if (file.getAuthorId().equals(data.getIdUser())) {
            Chat chat = chatRepository.findChatById(data.getIdChat());
            if (chat != null) {
                chat.getIdFiles().remove(file.getId());
                final Chat finalChat = chatRepository.save(chat);

                finalChat.getIdUsers().parallelStream()
                        .forEach(userId -> messageService.sendMessageToUser(userId,
                                new WsMessage(Command.FILE_DELETE,
                                        new FileData(userId,
                                                finalChat.getId(),
                                                file.getId(),
                                                file.getTitle(),
                                                data.getMultipartFile()
                                        )
                                ))
                        );
            } else {
                throw new UpdateChatException("There is no such chat");
            }
            fileRepository.delete(file);
        } else {
            throw new FileDeleteException("Only the author can delete message");
        }
    }
}
