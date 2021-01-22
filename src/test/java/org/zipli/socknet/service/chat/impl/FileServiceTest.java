package org.zipli.socknet.service.chat.impl;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.BsonValue;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.zipli.socknet.dto.FileData;
import org.zipli.socknet.exception.file.FileDeleteException;
import org.zipli.socknet.exception.file.SendFileException;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.FileRepository;
import org.zipli.socknet.repository.model.Chat;
import org.zipli.socknet.repository.model.File;
import org.zipli.socknet.repository.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class FileServiceTest {

    @Autowired
    FileService fileService;
    @MockBean
    FileRepository fileRepository;
    @MockBean
    ChatRepository chatRepository;
    @MockBean
    BsonValue value;
    @MockBean
    GridFsTemplate gridFsTemplate;
    @MockBean
    Document metadata;
    @MockBean
    Chat chat;
    private FileData fileData;
    private User user;

    @BeforeEach
    void setup() {

        fileData = new FileData(
                "userId",
                "chatId",
                "fileId",
                "title",
                "bytes".getBytes());

        user = new User("email@gmail.com", "password", "userName", "nickName");
        user.setId("userId");
    }

    @Test
    void sendFile_Pass() {

        Mockito.doReturn(new GridFSFile(value, "name", 1, 1, new Date(), metadata))
                .when(gridFsTemplate)
                .findOne(new Query(Criteria.where("_id").is(null)));

        Mockito.doReturn(chat)
                .when(chatRepository)
                .findChatById(fileData.getChatId());

        File fileAdd = new File(user.getId(), "chatId", new Date(), "title");
        fileAdd.setId("3");

        Mockito.doReturn(fileAdd)
                .when(fileRepository)
                .save(any());
        final File file = fileService.sendFile(fileData);

        assertEquals(fileData.getTitle(), file.getTitle());
        assertEquals(fileData.getUserId(), file.getAuthorId());
        assertEquals(fileData.getChatId(), file.getChatId());
    }

    @Test
    void sendFile_FailSendFileException() {

        assertThrows(SendFileException.class, () -> {
            fileService.sendFile(fileData);
        });
    }

//    @Test
//    void sendFile_FailUpdateChatException() {
//        Mockito.doReturn(new GridFSFile(value, "name", 1, 1, new Date(), metadata))
//                .when(gridFsTemplate)
//                .findOne(new Query(Criteria.where("_id").is(null)));
//
//        assertThrows(UpdateChatException.class, () -> {
//            fileService.sendFile(fileData);
//        });
//    }
//
//    @Test
//    void sendFile_FailSaveFileException() {
//        Mockito.doReturn(null)
//                .when(gridFsTemplate)
//                .findOne(new Query(Criteria.where("_id").is(null)));
//
//        assertThrows(SaveFileException.class, () -> {
//            fileService.sendFile(fileData);
//        });
//    }

    @Test
    void deleteFile_Pass() {

        List<String> idUsers = new ArrayList<>();
        idUsers.add(user.getId());
        idUsers.add("1");
        idUsers.add("2");
        ArrayList<String> fileIds = new ArrayList<>();

        Chat chat = new Chat("chatName", false, idUsers, "userId", fileIds);
        chat.setId("2");

        Mockito.doReturn(true)
                .when(chatRepository)
                .save(chat);

        File fileDelete = new File(user.getId(), chat.getId(), new Date(), "title");
        fileDelete.setId("3");

        Mockito.doReturn(fileDelete)
                .when(fileRepository)
                .getFileById(fileDelete.getId());

        Mockito.doReturn(chat)
                .when(chatRepository)
                .findChatById(chat.getId());

        chat.getIdFiles().add(fileDelete.getId());

        Chat finalChat = new Chat("chatName", false,
                Collections.singletonList(user.getId()), "userId");

        Mockito.doReturn(finalChat)
                .when(chatRepository)
                .save(chat);

        FileData data = new FileData(user.getId(), chat.getId(), fileDelete.getId(), "title");
        fileService.deleteFile(data);

        assertFalse(fileRepository.existsById(fileDelete.getId()));
    }

    @Test
    void deleteFile_FailFileDeleteException() {
        FileData data = new FileData(
                "wrongUser",
                null,
                "fileId",
                "title",
                "bytes".getBytes());

        assertThrows(FileDeleteException.class, () -> {
            fileService.deleteFile(data);
        });
    }
}
