package org.zipli.socknet.service.chat.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.dto.FullChatData;
import org.zipli.socknet.exception.chat.CreateChatException;
import org.zipli.socknet.exception.chat.DeleteChatException;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.MessageRepository;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.repository.model.Chat;
import org.zipli.socknet.repository.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class ChatServiceTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    MessageRepository messageRepository;
    private User user;
    private Chat chat;
    private FullChatData dataChat;
    @Autowired
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        user = new User("Email@com", "password", "Username", "MoiNik");
        user = userRepository.save(user);

        chat = new Chat("NameGroupChat", false, Collections.singletonList(user.getId()), user.getId());
        chat = chatRepository.save(chat);
        log.info(String.valueOf(chat));

        dataChat = new FullChatData(user.getId(),
                chat.getId(), "testChatName", new ArrayList<String>(), false);
    }

    @Test
    void createGroupChat_Pass() {

        Chat chat = chatService.createChat(dataChat);

        assertTrue(chatRepository.existsByChatName(chat.getChatName()));
        chatRepository.deleteAll();
    }

    @Test
    void createGroupChat_Fail() {

        try {
            Chat chatOne = chatService.createChat(dataChat);
            Chat chatTwo = chatService.createChat(dataChat);
        } catch (CreateChatException e) {
            assertEquals(e.getErrorStatusCode().getMessage(), "Chat already exists");
        }
        chatRepository.deleteAll();
    }

    @Test
    void removeChat_Pass() {

        User userOne = new User("dddddddd@com", "password", "dsadsadas", "MoiNik");
        userOne = userRepository.save(userOne);

        Chat chat = new Chat("NameChat", false, userOne.getId());
        chat.setUsersId(Collections.singletonList(userOne.getId()));
        chat = chatRepository.save(chat);

        userOne.setChatsId(Collections.singletonList(chat.getId()));
        userOne = userRepository.save(userOne);
        new FullChatData(user.getId(),
                chat.getId(),
                chat.getChatName());
        FullChatData dataTree = new FullChatData(userOne.getId(), chat.getId(), chat.getChatName());

        chatService.deleteChat(new ChatData(dataTree.getUserId(),dataTree.getChatId()));

        assertFalse(chatRepository.existsByChatName(chat.getChatName()));
        assertFalse(messageRepository.existsByChatId(chat.getId()));

        chatRepository.deleteAll();
    }

    @Test
    void removeChat_Fail() {

        FullChatData dataTree = new FullChatData("kakoitoId", chat.getId(), chat.getChatName());

        try {
            chatService.deleteChat(dataTree);
        } catch (DeleteChatException e) {
            assertEquals(e.getErrorStatusCode().getMessage(), "Only the creator can execute");
        }
    }

    @Test
    void joinChat() {
        User user = userRepository.save(new User("dasdasd", "gdsg", "dgsdg", "gdsg"));
        dataChat = new FullChatData(user.getId(),
                chat.getId(),
                "vgtunj");
        Chat chat = chatService.joinChat(dataChat);
        User userUpdate = userRepository.getUserById(user.getId());

        assertTrue(chat.getUsersId().contains(dataChat.getUserId()));
        assertTrue(userUpdate.getChatsId().contains(chat.getId()));
    }

    @Test
    void updateChat() {

        FullChatData fullChatData = new FullChatData(user.getId(), chat.getId(), "NewChatName");
        Chat chat = chatService.updateChat(fullChatData);

        assertEquals(chat.getChatName(), fullChatData.getChatName());
    }

    @Test
    void showChatsByUser() {

        List<Chat> chats = chatService.showChatsByUser(dataChat);

        assertEquals(user.getChatsId().size(), chats.size());
    }

    @Test
    void leaveChat() {

        Chat chat = new Chat("", true, user.getId());
        chat.getUsersId().add(user.getId());
        chat = chatRepository.save(chat);

        dataChat.setChatId(chat.getId());

        Chat newChat = chatService.leaveChat(dataChat);

        assertEquals(chat.getUsersId().size() - 1, newChat.getUsersId().size());

    }

}
