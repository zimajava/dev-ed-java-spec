package org.zipli.socknet.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zipli.socknet.dto.MessageData;
import org.zipli.socknet.repository.model.Message;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MessageRepositoryTest {

    private Message message;
    private Message message1;
    private Message message2;

    @Autowired
    MessageRepository messageRepository;

    @BeforeEach
    void setup() {
        message = new Message("authorId", "chatId",
                new Date(), "textMessage");
        message.setId("id");
        messageRepository.save(message);

        message1 = new Message("authorId1", "chatId",
                new Date(), "textMessage1");
        message1.setId("id1");
        messageRepository.save(message1);

        message2 = new Message("authorId2", "chatId",
                new Date(), "textMessage2");
        message2.setId("id2");
        messageRepository.save(message2);

    }

    @Test
    void getMessageById_Pass() {
        Message expected = message;

        Message actual = messageRepository.getMessageById(message.getId());

        assertEquals(expected, actual);
    }

    @Test
    void getMessageById_Fail() {
        assertNull(messageRepository.getMessageById("wrongId"));
    }

    @Test
    void existsByChatId_Pass() {
        assertTrue(messageRepository.existsByChatId(message.getChatId()));
    }

    @Test
    void existsByChatId_Fail() {
        assertFalse(messageRepository.existsByChatId("Fail"));
    }

    @Test
    void getMessageByIdAndAuthorId_Pass() {
        Message expected = message;

        Message actual = messageRepository.getMessageByIdAndAuthorId(message.getId(), message.getAuthorId());

        assertEquals(expected, actual);
    }

    @Test
    void getMessageByIdAndAuthorId_Fail() {
        Message expected = message;

        Message actual = messageRepository.getMessageByIdAndAuthorId(message.getId(), "asd");

        assertNotEquals(expected, actual);
    }

    @Test
    void existsById_Pass() {
        assertTrue(messageRepository.existsById(message.getId()));
    }

    @Test
    void existsById_Fail() {
        assertFalse(messageRepository.existsById("wrongId"));
    }

    @Test
    void save_Pass() {
        Message expected = new Message("testAuthorId", "testChatId", new Date(), "testTextMessage");

        Message actual = messageRepository.save(expected);

        assertEquals(expected, actual);
    }

    @Test
    void save_Fail() {
        assertThrows(IllegalArgumentException.class, () -> {
            messageRepository.save(null);
        });
    }

    @Test
    void updateMessage_Pass() {
        message.setTextMessage("NewMessage");

        Message expected = message;

        Message actual = messageRepository.updateMessage(new MessageData("authorId", "chatId", "id", "NewMessage"));

        assertEquals(expected, actual);
    }

    @Test
    void updateMessage_Fail() {
        Message actual = messageRepository.updateMessage(new MessageData("authorId", "chatId", "54645564", "NewMessage"));
        assertEquals(null, actual);
    }

    @Test
    void delete_Pass() {
        messageRepository.delete(message);
        assertFalse(messageRepository.existsById(message.getId()));
    }

    @Test
    void delete_Fail() {
        assertThrows(IllegalArgumentException.class, () -> {
            messageRepository.delete(null);
        });
    }

    @AfterEach
    @Test
    void deleteAllByChatId_Pass() {
        messageRepository.deleteAllByChatId(message.getChatId());
        assertFalse(messageRepository.existsByChatId(message.getChatId()));
    }
}
