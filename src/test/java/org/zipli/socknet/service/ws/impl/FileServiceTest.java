package org.zipli.socknet.service.ws.impl;

import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.sun.mail.iap.ByteArray;
import org.bson.*;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.zipli.socknet.dto.FileData;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.exception.chat.UpdateChatException;
import org.zipli.socknet.exception.message.MessageSendException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.File;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.FileRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileServiceTest {

    private FileData fileData;

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
    List<String> idFiles;
    @MockBean
    Document metadata;
//    @MockBean
//    GridFsResource gridFsResource;
//    @MockBean
//    ObjectId objectId;


    @BeforeEach
    void setup() {
        byte[] bytes = "string".getBytes();
        fileData = new FileData(
                "userId",
                "chatId",
                "fileId",
                "hello.txt",
                bytes);
    }

    @Test
    void sendFile_Pass() throws IOException {

        GridFSFile gridFSFile = Mockito.doReturn(new GridFSFile(value, "name", 1, 1, new Date(), metadata))
                .when(gridFsTemplate)
                .findOne(new Query(Criteria.where("_id").is(null)));

//        File fileTest1 = new File(fileData.getIdUser(), fileData.getIdChat(), new Date(), gridFsTemplate.getResource(new GridFSFile(value, "name", 1, 1, new Date(), metadata)).getInputStream());
//        File fileTest2 = new File(fileData.getIdUser(), fileData.getIdChat(), new Date(), fileData.getInputStream());
//        Mockito.when(fileTest1)

//                .thenReturn(fileTest2);
//        Mockito.when(gridFsTemplate.getResource(gridFSFile)
//                .getInputStream()).thenReturn(inputStream);

        User user = new User("email@gmail.com", "password", "userName", "nickName");
        user.setId("1");

        Mockito.doReturn(new Chat("chatName", false, Collections.singletonList(user.getId()), "userId"))
                .when(chatRepository)
                .findChatById(fileData.getIdChat());

        final File file = fileService.sendFile(fileData);

        assertEquals(fileData.getTitle(), file.getTitle());
        assertEquals(fileData.getIdUser(), file.getAuthorId());
        assertEquals(fileData.getIdChat(), file.getChatId());
    }

    @Test
    void sendFile_FailSendFileException() {

        assertThrows(SendFileException.class, () -> {
            fileService.sendFile(fileData);
        });
    }

    @Test
    void sendFile_FailMessageSendException() {
        Mockito.doReturn(new GridFSFile(value, "name", 1, 1, new Date(), metadata))
                .when(gridFsTemplate)
                .findOne(new Query(Criteria.where("_id").is(null)));

        assertThrows(MessageSendException.class, () -> {
            fileService.sendFile(fileData);
        });
    }

    @Test
    void sendFile_FailIOException() {

        assertThrows(IOException.class, () -> {
            fileService.sendFile(null);
        });
    }

    @Test
    void deleteFile_Pass() {
        User user = new User("email@gmail.com", "password", "userName", "nickName");
        user.setId("1");

        List<String> idUsers = new ArrayList<>();
        idUsers.add(user.getId());
        idUsers.add("1");
        idUsers.add("2");
        Chat chat = new Chat("chatName", false, idUsers, "userId");
        chat.setId("2");

        Mockito.doReturn(true)
                .when(chatRepository)
                .save(chat);

        File fileDelete = new File(user.getId(), chat.getId(), new Date(), "title");
        fileDelete.setId("3");

        Mockito.doReturn(true)
                .when(fileRepository)
                .save(fileDelete);

        Mockito.doReturn(fileDelete)
                .when(fileRepository)
                .getFileById(fileDelete.getId());

        Mockito.doReturn(chat)
                .when(chatRepository)
                .findChatById(chat.getId());

        chat.setIdFiles(idFiles);

        Mockito.doReturn(true)
                .when(idFiles)
                .remove(fileDelete.getId());

        Chat finalChat = new Chat("chatName", false, Collections.singletonList(user.getId()), "userId");

        Mockito.doReturn(finalChat)
                .when(chatRepository)
                .save(chat);

        FileData data = new FileData(user.getId(), chat.getId(), fileDelete.getId(), "title");
        fileService.deleteFile(data);

        assertFalse(fileRepository.existsById(fileDelete.getId()));
        assertFalse(chatRepository
                .findChatById(data.getIdChat())
                .getIdFiles()
                .contains(fileDelete.getId()));
    }

    @Test
    void deleteFile_FailFileDeleteException() {
        Mockito.doReturn(new File("wrongId", "chatId", new Date(), "hello.txt"))
                .when(fileRepository)
                .getFileById(fileData.getFileId());

        assertThrows(FileDeleteException.class, () -> {
            fileService.deleteFile(fileData);
        });
    }

    @Test
    void deleteFile_FailUpdateChatException() {
        Mockito.doReturn(new File("userId", "chatId", new Date(), "hello.txt"))
                .when(fileRepository)
                .getFileById(fileData.getFileId());
        Mockito.doReturn(new Chat())
                .when(chatRepository)
                .findChatById(fileData.getFileId());

        assertThrows(UpdateChatException.class, () -> {
            fileService.deleteFile(fileData);
        });
    }
}