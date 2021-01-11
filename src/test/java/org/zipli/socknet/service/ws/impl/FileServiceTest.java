package org.zipli.socknet.service.ws.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.zipli.socknet.dto.FileData;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.File;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.FileRepository;
import org.zipli.socknet.repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataMongoTest
class FileServiceTest {

    private FileData fileData;
    private File file;
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

//    @MockBean
//    InputStream inputStream;


    @BeforeEach
    void setup() {
//        MockMultipartFile multipartFile = new MockMultipartFile("Screenshot_1", "file:c:\\Screenshot_1.png".getBytes());

//        MultipartFile multipartFile =
//                new MockMultipartFile(
//                        "file",
//                        "test contract.pdf",
//                        MediaType.APPLICATION_PDF_VALUE,
//                        "<<pdf data>>".getBytes(StandardCharsets.UTF_8));

//
//        mockMvc.perform(
//                multipart("/users/")
//                        .file(file)
//                        .file(metadata)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().string("testcontract.pdf"));
//    }
        fileData = new FileData(
                "userId",
                "chatId",
                "fileId",
                "hello.txt",
                InputStream.nullInputStream());


//        MockMvc mockMvc
//                = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//        mockMvc.perform(multipart("/upload").file(file))
//                .andExpect(status().isOk());

//        Mockito.doReturn(new Chat("NameGroupChat", true, "userId"))
//                .when(chatRepository)
//                .findChatById("chatId");


    }

    @Test
    void sendFile_Pass() {

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
        User user = new User("email@gmail.com", "password", "userName", "nickName");
        user = userRepository.save(user);
        Chat chat = new Chat("NameOfChat", true, user.getId());
        chat.getIdUsers().add(user.getId());
        chat = chatRepository.save(chat);

        File fileDelete = new File(user.getId(), chat.getId(), new Date(), "title");
        chat.getIdFiles().add(fileDelete.getId());
        fileDelete = fileRepository.save(fileDelete);

        FileData data = new FileData(user.getId(), chat.getId(), fileDelete.getId(), "title");
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