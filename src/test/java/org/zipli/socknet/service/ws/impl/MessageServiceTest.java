package org.zipli.socknet.service.ws.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.zipli.socknet.dto.WsMessage;
import org.zipli.socknet.exception.CreateChatException;
import org.zipli.socknet.exception.RemoveChatException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.MessageRepository;
import org.zipli.socknet.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataMongoTest
class MessageServiceTest {

    private User user;
    private Chat chat;
    private WsMessage wsMessage;
    private MessagerService messageService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    ChatRepository chatRepository;

    @BeforeEach
    void setUp() {

        messageService = new MessagerService(userRepository, chatRepository, messageRepository);

        user = new User("Email@com", "password", "Username", "MoiNik");
        user = userRepository.save(user);

        chat = new Chat("NameGroupChat", true, user.getId());

        chat = chatRepository.save(chat);

        wsMessage = new WsMessage("textMessage", user.getId(), chat.getId(), "", "ChatName");
    }

    @Test
    void sendMessage() {

        Message message = messageService.sendMessage(wsMessage);

        assertEquals(wsMessage.getTextMessage(), message.getTextMessage());
        assertEquals(wsMessage.getUserId(), message.getAuthorId());
        assertEquals(wsMessage.getChatId(), message.getChatId());
    }

    @Test
    void createGroupChatPass() {

        Chat chat = messageService.createGroupChat(wsMessage);

        assertTrue(chatRepository.existsByChatName(chat.getChatName()));
        chatRepository.deleteAll();
    }

    @Test
    void createGroupChatFail() {

        try {
            Chat chatOne = messageService.createGroupChat(wsMessage);
            Chat chatTwo = messageService.createGroupChat(wsMessage);
        } catch (CreateChatException e) {
            assertEquals(e.getMessage(), "Such a chat already exists");
        }
        chatRepository.deleteAll();
    }

    @Test
    void createPrivateChatPass() {

        userRepository.save(new User("kkkk@gma.vv", "ghjk", "teaama", "morgen"));
        wsMessage.setUserName("teaama");
        Chat chat = messageService.createPrivateChat(wsMessage);

        assertTrue(chatRepository.existsByChatName(chat.getChatName()));
        assertEquals(chat.getIdUsers().size(), 2);
        chatRepository.deleteAll();
    }

    @Test
    void removeChatPass() {

        User userOne = new User("dddddddd@com", "password", "dsadsadas", "MoiNik");
        userOne = userRepository.save(userOne);

        Chat chat = new Chat("NameChat", false, userOne.getId());
        chat.setIdUsers(Collections.singletonList(userOne.getId()));
        chat = chatRepository.save(chat);

        userOne.setChatsId(Collections.singletonList(chat.getId()));
        userOne = userRepository.save(userOne);

        WsMessage wsMessageTree = new WsMessage("textMessage", userOne.getId(), chat.getId(), "", chat.getChatName());

        messageService.removeChat(wsMessageTree);

        assertFalse(chatRepository.existsByChatName(chat.getChatName()));
        assertFalse(messageRepository.existsByChatId(chat.getId()));

        chatRepository.deleteAll();
    }

    @Test
    void removeChatFail() {

        WsMessage wsMessageTree = new WsMessage("textMessage", "kakoitoId", chat.getId(), "", chat.getChatName());

        try {
            messageService.removeChat(wsMessageTree);
        } catch (RemoveChatException e) {
            assertEquals(e.getMessage(), "Only the creator can delete");
        }
    }


    @Test
    void joinChat() {

        Chat chat = messageService.joinChat(wsMessage);
        User userUpdate = userRepository.getUserById(user.getId());

        assertTrue(chat.getIdUsers().contains(wsMessage.getUserId()));
        assertTrue(userUpdate.getChatsId().contains(chat.getId()));
    }

    @Test
    void updateChat() {

        WsMessage wsMessage = new WsMessage("textMessage", user.getId(), chat.getId(), "", "NewChatName");
        Chat chat = messageService.updateChat(wsMessage);

        assertEquals(chat.getChatName(), wsMessage.getNameChat());
    }

    @Test
    void showChatsByUser() {

        List<Chat> chats = messageService.showChatsByUser(wsMessage);

        assertEquals(user.getChatsId().size(), chats.size());
    }

    @Test
    void leaveChat() {

        Chat chat = new Chat("", true, user.getId());
        chat.getIdUsers().add(user.getId());
        chat = chatRepository.save(chat);

        wsMessage.setChatId(chat.getId());

        Chat newChat = messageService.leaveChat(wsMessage);

        assertEquals(chat.getIdUsers().size() - 1, newChat.getIdUsers().size());

    }

    @Test
    void getMessages() {

        Chat chat = new Chat("ChatBuGetMessage", false, user.getId());
        chat = chatRepository.save(chat);

        Message messageOne = new Message(user.getId(),chat.getId(),new Date(),"dasdfs");
        Message messageTwo = new Message(user.getId(),chat.getId(),new Date(),"dsadgbv");
        Message messageTree = new Message(user.getId(),chat.getId(),new Date(),"bvbvbvvc");

        messageOne = messageRepository.save(messageOne);
        messageTwo = messageRepository.save(messageTwo);
        messageTree =messageRepository.save(messageTree);

        chat.getIdMessages().add(messageOne.getId());
        chat.getIdMessages().add(messageTwo.getId());
        chat.getIdMessages().add(messageTree.getId());

        chat = chatRepository.save(chat);

        wsMessage.setChatId(chat.getId());
        List<Message> messages = messageService.getMessages(wsMessage);

        assertEquals(messages.size(),3);

    }
}