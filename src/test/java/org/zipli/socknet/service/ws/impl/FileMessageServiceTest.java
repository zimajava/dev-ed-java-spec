package org.zipli.socknet.service.ws.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.zipli.socknet.dto.FileData;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.FileMessage;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.FileRepository;
import org.zipli.socknet.repository.UserRepository;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataMongoTest
class FileMessageServiceTest {

    private FileData fileData;
    private Chat chat;

    @MockBean
    FileService fileService;

    @Autowired
    FileRepository fileRepository;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    UserRepository userRepository;

    @MockBean
    GridFsTemplate gridFsTemplate;

//    @MockBean
//    InputStream inputStream;


    @BeforeEach
    void setup() {
        MockMultipartFile multipartFile = new MockMultipartFile("Screenshot_1", "Screenshot_1.png".getBytes());

        fileData = new FileData(
                "userId",
                "chatId",
                "fileId",
                "title",
                multipartFile);

//        Mockito.doReturn(new Chat("NameGroupChat", true, "userId"))
//                .when(chatRepository)
//                .findChatById("chatId");


    }

    @Test
    void sendFile_Pass() {

        FileMessage fileMessage = null;
        try {
            fileMessage = fileService.sendFile(fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(fileData.getTitle(), fileMessage.getTitle());
        assertEquals(fileData.getIdUser(), fileMessage.getAuthorId());
        assertEquals(fileData.getIdChat(), fileMessage.getChatId());
    }

    @Test
    void sendFile_FailSendFileException() {//переделать
//        Mockito.doReturn(null)
//                .when(gridFsTemplate)
//                .findOne(new Query(Criteria.where("_id").is("id")));
        try {
            fileService.sendFile(fileData);
        } catch (SendFileException | IOException e) {
            assertEquals(e.getMessage(), "GridFsFile is null!");
        }
    }

    @Test
    void sendFile_FailIOException() {

        try {
            fileService.sendFile(null);
        } catch (IOException e) {
            assertEquals(e.getMessage(), "Exception while updating message");
        }
    }

    @Test
    void deleteFile_Pass() {
        User user = new User("jhk@gmail.com", "kji", "khi", "kji");
        user = userRepository.save(user);
        Chat chat = new Chat("NameGroupChat", true, user.getId());
        chat.getIdUsers().add(user.getId());
        chat = chatRepository.save(chat);

        FileMessage fileMessageDelete = new FileMessage(user.getId(), chat.getId(), new Date(), "title");
        fileMessageDelete = fileRepository.save(fileMessageDelete);

        FileData data = new FileData(user.getId(), chat.getId(), fileMessageDelete.getId(), "title");
        fileService.deleteFile(data);

        assertFalse(fileRepository.existsById(fileMessageDelete.getId()));
        assertFalse(chatRepository
                .findChatById(data.getIdChat())
                .getIdFiles()
                .contains(fileMessageDelete.getId()));
    }

    @Test
    void deleteFile_FailUpdateChatException() {
        FileMessage fileMessageDelete = new FileMessage(fileData.getIdUser(), fileData.getIdChat(), new Date(), fileData.getTitle());
        fileMessageDelete = fileRepository.save(fileMessageDelete);

        FileData data = new FileData(fileData.getIdUser(), fileData.getIdChat(), fileMessageDelete.getId(), fileData.getTitle());

        try {
            fileService.deleteFile(data);
        } catch (FileDeleteException e) {
            assertEquals(e.getMessage(), "Only the author can delete the file");
        }
    }

    @Test
    void deleteFile_FailDeleteFileException() {
        FileMessage fileMessageDelete = new FileMessage(fileData.getIdUser(), fileData.getIdChat(), new Date(), fileData.getTitle());
        fileMessageDelete = fileRepository.save(fileMessageDelete);

        FileData data = new FileData(fileData.getIdUser(), " ", fileMessageDelete.getId(), fileData.getTitle());

        try {
            fileService.deleteFile(data);
        } catch (UpdateChatException e) {
            assertEquals(e.getMessage(), "There is no such chat");
        }
    }
}