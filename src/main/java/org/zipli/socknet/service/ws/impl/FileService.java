package org.zipli.socknet.service.ws.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.sun.mail.iap.ByteArray;
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
import org.zipli.socknet.exception.message.MessageSendException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.File;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.FileRepository;
import org.zipli.socknet.service.ws.IFileService;
import org.zipli.socknet.service.ws.message.impl.MessageService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

@Service
public class FileService implements IFileService {

    private final ChatRepository chatRepository;
    private final FileRepository fileRepository;
    private final MessageService messageService;
    private final GridFsTemplate gridFsTemplate;
//    private final GridFsOperations operations;

    public FileService(ChatRepository chatRepository, FileRepository fileRepository,
                       MessageService messageService, GridFsTemplate gridFsTemplate) {
        this.chatRepository = chatRepository;
        this.fileRepository = fileRepository;
        this.messageService = messageService;
        this.gridFsTemplate = gridFsTemplate;
//        this.operations = operations;
    }


    @Override
    public File sendFile(FileData data) throws SendFileException, IOException {
        File file;
        Chat chat;
        if (data != null) {
            DBObject metaData = new BasicDBObject();
            metaData.put("type", "file");
            metaData.put("title", data.getTitle());
            ByteArray byteArray = new ByteArray(data.getBytes().length);
            ByteArrayInputStream inputStream = byteArray.toByteArrayInputStream();
            ObjectId id = gridFsTemplate.store(
                    inputStream,
                    data.getTitle(),
                    metaData);

            GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
            if (gridFSFile != null) {
                file = new File(data.getIdUser(), data.getIdChat(), new Date(), inputStream);
//                        operations.getResource(gridFSFile).getInputStream());

                final File finalFile = fileRepository.save(file);
                chat = chatRepository.findChatById(data.getIdChat());
                if (chat != null) {
                    chat.getIdFiles().add(file.getId());

                    chat.getIdUsers().parallelStream()
                            .forEach(userId -> messageService.sendMessageToUser(userId,
                                    new WsMessageResponse(Command.FILE_SEND,
                                            new FileData(userId,
                                                    chat.getId(),
                                                    finalFile.getId(),
                                                    finalFile.getTitle(),
                                                    data.getBytes())
                                    ))
                            );
                } else {
                    throw new MessageSendException("Chat doesn't exist");
                }
            } else {
                throw new SendFileException("GridFSFile is null!");
            }

            chatRepository.save(chat);
            return file;

        } else {
            throw new IOException("Data is null!");
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
                                new WsMessageResponse(Command.FILE_DELETE,
                                        new FileData(userId,
                                                finalChat.getId(),
                                                file.getId(),
                                                file.getTitle(),
                                                data.getBytes()
                                        )
                                ))
                        );
            } else {
                throw new UpdateChatException("There is no such chat",
                        WsException.CHAT_NOT_EXIT.getNumberException());
            }
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(data.getFileId())));
            fileRepository.delete(file);
        } else {
            throw new FileDeleteException("Only the author can delete the file");
        }
    }
}
