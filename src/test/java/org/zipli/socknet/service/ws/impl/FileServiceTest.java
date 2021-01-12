package org.zipli.socknet.service.ws.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.*;
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
import org.zipli.socknet.dto.MessageData;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.File;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.FileRepository;
import org.zipli.socknet.repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    UserRepository userRepository;

    @MockBean
    GridFsTemplate gridFsTemplate;

    @MockBean
    InputStream inputStream;


    @BeforeEach
    void setup() {

        fileData = new FileData(
                "userId",
                "chatId",
                "fileId",
                "hello.txt",
                inputStream);
    }

    @Test
    void sendFile_Pass() throws IOException {
        Mockito.doReturn(new Chat("NameGroupChat", true, fileData.getIdUser()))
                .when(chatRepository)
                .findChatById("chatId");

//        byte[] bytes = {(byte) 0xCF, (byte) 0xF0, (byte) 0xE8, (byte) 0xE2, (byte) 0xE5, (byte) 0xF2,
//                (byte) 0xCF, (byte) 0xF0, (byte) 0xE8, (byte) 0xE2, (byte) 0xE5, (byte) 0xF2};
//
//       ObjectId id = Mockito.doReturn(bytes)
//                .when(gridFsTemplate)
//                .store(fileData.getInputStream(), fileData.getTitle());

        Map<String, Object> result =
                new ObjectMapper().readValue(
                        "{\"rollNumber\":21 , \"firstName\":\"Saurabh\" , \"lastName\":\"Gupta\"}", HashMap.class);

        BSONObject object = new BasicBSONObject();
        object.putAll(result);
//        BsonValue id= get()

        Mockito.doReturn(new GridFSFile((BsonValue) object, "kkk", 3, 3, new Date(), null))
                .when(gridFsTemplate)
                .findOne(new Query(Criteria.where("_id").is(1)));

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
    void sendFile_FailIOException() {

        assertThrows(IOException.class, () -> {
            fileService.sendFile(null);
        });
    }

    @Test
    void deleteFile_Pass() {
        Chat chat = new Chat("chatName", true, "userId");
//       final Chat chat = chatRepository.save(chatt);
        User user = new User("email@gmail.com", "password", "userName", "nickName");
//final User user = userRepository.save(userr);
        Mockito.doReturn("chatId")
                .when(chatRepository)
                .save(chat);
        Mockito.doReturn(chat)
                .when(chatRepository)
                .findChatById(fileData.getIdChat());
////        final
        File fileDelete = new File(chat.getCreatorUserId(), chat.getId(), new Date(12), "hello.txt");
    final File file= fileRepository.save(fileDelete);
        FileData data = new FileData(user.getId(), chat.getId(), file.getId(), "dsad");
//        Mockito.doReturn(fileDelete)
//                .when(fileRepository)
//                .getFileById(fileData.getFileId());
        fileService.deleteFile(data);

        assertFalse(fileRepository.existsById(fileDelete.getId()));
//        assertFalse(chatRepository
//                .findChatById(data.getIdChat())
//                .getIdFiles()
//                .contains(fileDelete.getId()));
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