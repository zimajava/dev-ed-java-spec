package org.zipli.socknet.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zipli.socknet.repository.model.Chat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class ChatRepositoryTest {

    private Chat chat;

    @Autowired
    ChatRepository chatRepository;

    @BeforeEach
    void setup() {
        chat = new Chat("chatName", false,
                Collections.singletonList("userId"), "userId");
        chat.setId("id");
        chatRepository.save(chat);
    }

    @Test
    void save_Pass() {
        chatRepository.deleteAll();
        chatRepository.save(chat);
        assertEquals(chatRepository.findChatById(chat.getId()).toString(), chat.toString());
    }

    @Test
    void save_Fail() {
        assertThrows(IllegalArgumentException.class, () -> {
            chatRepository.save(null);
        });
    }

    @Test
    void deleteAll_Pass() {
        chatRepository.deleteAll();
        assertFalse(chatRepository.existsByChatName(chat.getChatName()));
    }

    @Test
    void findChatById_Pass() {
        assertEquals(chatRepository.findChatById(chat.getId()).toString(), chat.toString());
    }

    @Test
    void findChatById_Fail() {
        assertNull(chatRepository.findChatById("wrongId"));
    }

    @Test
    void existsByChatName_Pass() {
        assertTrue(chatRepository.existsByChatName(chat.getChatName()));
    }

    @Test
    void existsByChatName_Fail() {
        assertFalse(chatRepository.existsByChatName("wrongName"));
    }

    @Test
    void deleteById_Pass() {
        chatRepository.deleteById(chat.getId());
        assertFalse(chatRepository.existsByChatName(chat.getChatName()));
    }

    @Test
    void deleteById_Fail() {
        chatRepository.deleteById(null);
        assertTrue(chatRepository.existsByChatName(chat.getChatName()));
    }

    @Test
    void getChatsByIdIn_Pass() {
        Collection<String> listOfIds = new ArrayList<>();
        listOfIds.add(chat.getId());
        List<Chat> expectedChats = new ArrayList<>();
        expectedChats.add(chat);
        assertEquals(chatRepository.getChatsByIdIn(listOfIds).toString(), expectedChats.toString());
    }

    @Test
    void getChatsByIdIn_Fail() {
        Collection<String> listOfIds = new ArrayList<>();
        listOfIds.add("wrongId");
        List<Chat> expectedChats = new ArrayList<>();
        assertEquals(chatRepository.getChatsByIdIn(listOfIds).toString(), expectedChats.toString());
    }

    @AfterEach
    void afterEach() {
        chatRepository.deleteAll();
    }
}