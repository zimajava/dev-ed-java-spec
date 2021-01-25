package org.zipli.socknet.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zipli.socknet.repository.model.File;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileRepositoryTest {

    private File file;

    @Autowired
    FileRepository fileRepository;

    @BeforeEach
    void setup() {
        file = new File("userID", "chatId",
                new Date(), "title", null);
        file.setId("id");
        fileRepository.save(file);
    }

    @Test
    void getFileById_Pass() {
        assertEquals(fileRepository.getFileById(file.getId()).toString(), file.toString());
    }

    @Test
    void getFileById_Fail() {
        assertNull(fileRepository.getFileById("wrongId"));
    }

    @Test
    void save_Pass() {
        fileRepository.deleteById(file.getId());
        fileRepository.save(file);
        assertEquals(fileRepository.getFileById(file.getId()).toString(), file.toString());
    }

    @Test
    void save_Fail() {
        assertThrows(IllegalArgumentException.class, () -> {
            fileRepository.save(null);
        });
    }

    @Test
    void existsById_Pass() {
        assertTrue(fileRepository.existsById(file.getId()));
    }

    @Test
    void existsById_Fail() {
        assertFalse(fileRepository.existsById("wrongId"));
    }

    @Test
    void deleteById_Pass() {
        fileRepository.deleteById(file.getId());
        assertFalse(fileRepository.existsById(file.getId()));
    }

    @Test
    void deleteById_Fail() {
        fileRepository.deleteById(null);
        assertTrue(fileRepository.existsById(file.getId()));
    }

    @AfterEach
    void afterEach() {
        fileRepository.deleteById(file.getId());
    }
}