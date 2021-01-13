package org.zipli.socknet.service.ws.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.dto.ChatGroupData;
import org.zipli.socknet.exception.chat.CreateChatException;
import org.zipli.socknet.exception.chat.DeleteChatException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.MessageRepository;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import reactor.core.publisher.Sinks;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@DataMongoTest
class ChatServiceTest {
    private User user;
    private Chat chat;

    private ChatData dataChat;
    private ChatService chatService;
    private ChatGroupData chatGroupData;

    @Autowired
    UserRepository userRepository;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    MessageRepository messageRepository;

    EmitterService emitterService = new EmitterService(userRepository,new JwtUtils());

    @BeforeEach
    void setUp() {
        chatService = new ChatService(userRepository,chatRepository,messageRepository,emitterService);
        user = new User("Email@com", "password", "Username", "MoiNik");
        user = userRepository.save(user);

        chat = new Chat("NameGroupChat", false,new ArrayList<>(), Collections.singletonList(user.getId()), user.getId());
        chat = chatRepository.save(chat);
        log.info(String.valueOf(chat));

        dataChat = new ChatData(user.getId(),
                chat.getId(),
                "vgtunj");
        chatGroupData = new ChatGroupData(user.getId(), "1", "NameGroupChatOther",new ArrayList<>());
    }

    @Test
    void createGroupChat_Pass() {

        Chat chat = chatService.createGroupChat(chatGroupData);

        assertTrue(chatRepository.existsByChatName(chat.getChatName()));
        chatRepository.deleteAll();
    }

    @Test
    void createGroupChat_Fail() {

        try {
            Chat chatOne = chatService.createGroupChat(chatGroupData);
            Chat chatTwo = chatService.createGroupChat(chatGroupData);
        } catch (CreateChatException e) {
            assertEquals(e.getMessage(), "Such a chat already exists");
        }
        chatRepository.deleteAll();
    }

    @Test
    void createPrivateChat_Pass() {

        User user = userRepository.save(new User("kkkk@gma.vv", "ghjk", "teaama", "morgen"));
        dataChat.setSecondUserId(user.getId());
        Chat chat = chatService.createPrivateChat(dataChat);

        assertTrue(chatRepository.existsByChatName(chat.getChatName()));
        assertEquals(chat.getIdUsers().size(), 2);
        chatRepository.deleteAll();
    }

    @Test
    void removeChat_Pass() {

        User userOne = new User("dddddddd@com", "password", "dsadsadas", "MoiNik");
        userOne = userRepository.save(userOne);

        Chat chat = new Chat("NameChat", false, userOne.getId());
        chat.setIdUsers(Collections.singletonList(userOne.getId()));
        chat = chatRepository.save(chat);

        userOne.setChatsId(Collections.singletonList(chat.getId()));
        userOne = userRepository.save(userOne);
        new ChatData(user.getId(),
                chat.getId(),
                chat.getChatName());
        ChatData dataTree = new ChatData(userOne.getId(), chat.getId(), chat.getChatName());

        chatService.deleteChat(dataTree);

        assertFalse(chatRepository.existsByChatName(chat.getChatName()));
        assertFalse(messageRepository.existsByChatId(chat.getId()));

        chatRepository.deleteAll();
    }

    @Test
    void removeChat_Fail() {

        ChatData dataTree = new ChatData("kakoitoId", chat.getId(), chat.getChatName());

        try {
            chatService.deleteChat(dataTree);
        } catch (DeleteChatException e) {
            assertEquals(e.getMessage(), "Only the author can delete chat");
        }
    }

    @Test
    void joinChat() {
        User user = userRepository.save(new User("dasdasd","gdsg","dgsdg","gdsg"));
        dataChat = new ChatData(user.getId(),
                chat.getId(),
                "vgtunj");
        Chat chat = chatService.joinChat(dataChat);
        User userUpdate = userRepository.getUserById(user.getId());

        assertTrue(chat.getIdUsers().contains(dataChat.getIdUser()));
        assertTrue(userUpdate.getChatsId().contains(chat.getId()));
    }

    @Test
    void updateChat() {

        ChatData chatData = new ChatData(user.getId(), chat.getId(), "NewChatName");
        Chat chat = chatService.updateChat(chatData);

        assertEquals(chat.getChatName(), chatData.getChatName());
    }

    @Test
    void showChatsByUser() {

        List<Chat> chats = chatService.showChatsByUser(dataChat);

        assertEquals(user.getChatsId().size(), chats.size());
    }

    @Test
    void leaveChat() {

        Chat chat = new Chat("", true, user.getId());
        chat.getIdUsers().add(user.getId());
        chat = chatRepository.save(chat);

        dataChat.setIdChat(chat.getId());

        Chat newChat = chatService.leaveChat(dataChat);

        assertEquals(chat.getIdUsers().size() - 1, newChat.getIdUsers().size());

    }


}