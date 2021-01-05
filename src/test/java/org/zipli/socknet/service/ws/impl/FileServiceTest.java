package org.zipli.socknet.service.ws.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.zipli.socknet.dto.FileData;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.File;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.FileRepository;
import java.io.IOException;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataMongoTest
class FileServiceTest {

    private FileData fileData;
    private Chat chat;

    @MockBean
    FileService fileService;

    @Autowired
    FileRepository fileRepository;
    @Autowired
    ChatRepository chatRepository;

    @Autowired
    GridFsTemplate gridFsTemplate;
//    @Autowired
//    GridFsOperations operations;

//    @MockBean
//    InputStream inputStream;


    @BeforeEach
    void setUp() {
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
//        chat = new Chat("NameGroupChat", true, "userId");
//        chat = chatRepository.save(chat);
    }

    @Test
    void sendFile_Pass() {

        File file = null;
        try {
            file = fileService.sendFile(fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(fileData.getTitle(), file.getTitle());
        assertEquals(fileData.getIdUser(), file.getAuthorId());
        assertEquals(fileData.getIdChat(), file.getChatId());
    }

    @Test
    void sendFile_FailSendFileException() {

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
    void sendFile_FailIOException() {

//        GridFSFile gridFSFile = new GridFSFile("", "lug", 12, 12, null, null);

        assertThrows(IOException.class, () -> {
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
        File fileDelete = new File("authorId", "chatId", new Date(), "title");
        fileDelete = fileRepository.save(fileDelete);

        FileData data = new FileData("userId", "chatId", fileDelete.getId(), "title");
        fileService.deleteFile(data);

        assertFalse(fileRepository.existsById(fileDelete.getId()));
        assertFalse(chatRepository
                .findChatById(data.getIdChat())
                .getIdFiles()
                .contains(fileDelete.getId()));
    }

    @Test
    void deleteFile_FailUpdateChatException() {
        File fileDelete = new File(fileData.getIdUser(), fileData.getIdChat(), new Date(), fileData.getTitle());
        fileDelete = fileRepository.save(fileDelete);

        FileData data = new FileData(fileData.getIdUser(), fileData.getIdChat(), fileDelete.getId(), fileData.getTitle());

        try {
            fileService.deleteFile(data);
        } catch (FileDeleteException e) {
            assertEquals(e.getMessage(), "Only the author can delete the file");
        }
    }

    @Test
    void deleteFile_FailDeleteFileException() {
        File fileDelete = new File(fileData.getIdUser(), fileData.getIdChat(), new Date(), fileData.getTitle());
        fileDelete = fileRepository.save(fileDelete);

        FileData data = new FileData(fileData.getIdUser(), " ", fileDelete.getId(), fileData.getTitle());

        try {
            fileService.deleteFile(data);
        } catch (UpdateChatException e) {
            assertEquals(e.getMessage(), "There is no such chat");
        }
    }
}