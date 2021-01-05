package org.zipli.socknet.service.ws.impl;

import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonValue;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.dto.FileData;
import org.zipli.socknet.dto.MessageData;
import org.zipli.socknet.exception.SendFileException;
import org.zipli.socknet.exception.UserNotFoundException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.File;
import org.zipli.socknet.model.Message;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.FileRepository;
import org.zipli.socknet.repository.MessageRepository;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import reactor.core.publisher.Sinks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;

@Slf4j
@DataMongoTest
class FileServiceTest {

    //    @MockBean
    private FileData fileData;

    @MockBean
    FileService fileService;
//    private MessageService messageService;
//    private File file;

    //    @Autowired
//    UserRepository userRepository;
    @Autowired
    FileRepository fileRepository;
    @Autowired
    ChatRepository chatRepository;
    //        @Autowired
//    MessageService messageService;
    @Autowired
    GridFsTemplate gridFsTemplate;
//    @Autowired
//    GridFsOperations operations;

//    @MockBean
//    InputStream inputStream;


    @BeforeEach
    void setUp() {
        MockMultipartFile multipartFile = new MockMultipartFile("Screenshot_1", "Screenshot_1.png".getBytes());
//        fileService = new FileService();
        fileData = new FileData(
                "userId",
                "chatId",
                "fileId",
                "title",
                multipartFile);
    }

    @Test
    void sendFile_Pass() {

        File file = null;
        try {
            file = fileService.sendFile(fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {

//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        assertEquals(fileData.getTitle(), file.getTitle());
        assertEquals(fileData.getIdUser(), file.getAuthorId());
        assertEquals(fileData.getIdChat(), file.getChatId());
    }

    @Test
    void sendFile_Fail() {

//        GridFSFile gridFSFile = new GridFSFile("", "lug", 12, 12, null, null);

        assertThrows(SendFileException.class, () -> {
            fileService.sendFile(new FileData(
                    null,
                    null,
                    null,
                    null,
                    null));
        });
    }

    @Test
    void deleteFile_Pass() {
        File fileDelete = new File(fileData.getIdUser(), fileData.getIdChat(), new Date(), fileData.getTitle());
        fileDelete = fileRepository.save(fileDelete);

        FileData data = new FileData(fileData.getIdUser(), fileData.getIdChat(), fileDelete.getId(), fileData.getTitle());
        fileService.deleteFile(fileData);

       assertFalse(fileRepository.existsById(fileDelete.getId()));
        assertFalse(chatRepository
                .findChatById(data.getIdChat())
                .getIdMessages()
                .contains(fileDelete.getId()));
    }

    @Test
    void deleteFile_FailUpdateChatException() {

    }

    @Test
    void deleteFile_FailDeleteFileException() {
    }
}