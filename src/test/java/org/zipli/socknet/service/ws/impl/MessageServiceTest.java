package org.zipli.socknet.service.ws.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.zipli.socknet.dto.Data;
import org.zipli.socknet.dto.DataChat;
import org.zipli.socknet.dto.DataMessage;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.MessageRepository;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataMongoTest
class MessageServiceTest {

    private User user;
    private Chat chat;
    private DataMessage dataMessage;
    private DataChat dataChat;
    private MessageService messageService;
    private Message message;

    @Autowired
    UserRepository userRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    ChatRepository chatRepository;

    @BeforeEach
    void setUp() {

        messageService = new MessageService(userRepository, chatRepository, messageRepository, new JwtUtils());
        user = new User("Email@com", "password", "Username", "MoiNik");
        user = userRepository.save(user);

        chat = new Chat("NameGroupChat", true, user.getId());
        chat = chatRepository.save(chat);
        dataMessage = new DataMessage(
                user.getId(),
                chat.getId(),
                "dsadsda",
                "");
        dataChat = new DataChat(user.getId(),
                chat.getId(),
                "vgtunj");
        message = new Message(user.getId(), chat.getId(), new Date(), "dsadsadsadsads");
    }

    @Test
    void sendMessage() {

        Message message = messageService.sendMessage(dataMessage);

        assertEquals(dataMessage.getTextMessage(), message.getTextMessage());
        assertEquals(dataMessage.getIdUser(), message.getAuthorId());
        assertEquals(dataMessage.getIdChat(), message.getChatId());
    }

    @Test
    void createGroupChat_Pass() {

        Chat chat = messageService.createGroupChat(dataChat);

        assertTrue(chatRepository.existsByChatName(chat.getChatName()));
        chatRepository.deleteAll();
    }

    @Test
    void createGroupChat_Fail() {

        try {
            Chat chatOne = messageService.createGroupChat(dataChat);
            Chat chatTwo = messageService.createGroupChat(dataChat);
        } catch (CreateChatException e) {
            assertEquals(e.getMessage(), "Such a chat already exists");
        }
        chatRepository.deleteAll();
    }

    @Test
    void createPrivateChat_Pass() {

        User user = userRepository.save(new User("kkkk@gma.vv", "ghjk", "teaama", "morgen"));
        dataChat.setSecondUserId(user.getId());
        Chat chat = messageService.createPrivateChat(dataChat);

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
        new DataChat(user.getId(),
                chat.getId(),
                chat.getChatName());
        DataChat dataTree = new DataChat(userOne.getId(), chat.getId(), chat.getChatName());

        messageService.removeChat(dataTree);

        assertFalse(chatRepository.existsByChatName(chat.getChatName()));
        assertFalse(messageRepository.existsByChatId(chat.getId()));

        chatRepository.deleteAll();
    }

    @Test
    void removeChat_Fail() {

        DataChat dataTree = new DataChat("kakoitoId", chat.getId(), chat.getChatName());

        try {
            messageService.removeChat(dataTree);
        } catch (RemoveChatException e) {
            assertEquals(e.getMessage(), "Only the creator can delete");
        }
    }

    @Test
    void joinChat() {

        Chat chat = messageService.joinChat(dataChat);
        User userUpdate = userRepository.getUserById(user.getId());

        assertTrue(chat.getIdUsers().contains(dataChat.getIdUser()));
        assertTrue(userUpdate.getChatsId().contains(chat.getId()));
    }

    @Test
    void updateChat() {

        DataChat dataChat = new DataChat(user.getId(), chat.getId(), "NewChatName");
        Chat chat = messageService.updateChat(dataChat);

        assertEquals(chat.getChatName(), dataChat.getChatName());
    }

    @Test
    void showChatsByUser() {

        List<Chat> chats = messageService.showChatsByUser(dataChat);

        assertEquals(user.getChatsId().size(), chats.size());
    }

    @Test
    void leaveChat() {

        Chat chat = new Chat("", true, user.getId());
        chat.getIdUsers().add(user.getId());
        chat = chatRepository.save(chat);

        dataChat.setIdChat(chat.getId());

        Chat newChat = messageService.leaveChat(dataChat);

        assertEquals(chat.getIdUsers().size() - 1, newChat.getIdUsers().size());

    }

    @Test
    void getMessages() {

        Chat chat = new Chat("ChatBuGetMessage", false, user.getId());
        chat = chatRepository.save(chat);

        Message messageOne = new Message(user.getId(), chat.getId(), new Date(), "dasdfs");
        Message messageTwo = new Message(user.getId(), chat.getId(), new Date(), "dsadgbv");
        Message messageTree = new Message(user.getId(), chat.getId(), new Date(), "bvbvbvvc");

        messageOne = messageRepository.save(messageOne);
        messageTwo = messageRepository.save(messageTwo);
        messageTree = messageRepository.save(messageTree);

        chat.getIdMessages().add(messageOne.getId());
        chat.getIdMessages().add(messageTwo.getId());
        chat.getIdMessages().add(messageTree.getId());

        chat = chatRepository.save(chat);

        dataMessage.setIdChat(chat.getId());
        List<Message> messages = messageService.getMessages(dataMessage);

        assertEquals(messages.size(), 3);

        assertEquals(messages.get(0).getMessage(), messageOne.getMessage());
        assertEquals(messages.get(1).getMessage(), messageTwo.getMessage());
        assertEquals(messages.get(2).getMessage(), messageTree.getMessage());

    }

    @Test
    void updateMessage_Pass() {

        message = messageRepository.save(message);

        DataMessage data = new DataMessage(user.getId(), chat.getId(), message.getId(), "dsad");
        Message updateMessage = messageService.updateMessage(data);

        assertEquals(updateMessage.getTextMessage(), data.getTextMessage());
        assertEquals(updateMessage.getId(), data.getMessageId());
    }

    @Test
    void updateMessage_Fail() {

        message = messageRepository.save(message);

        User usernew = userRepository.save(new User("akkka@fsa.cas", "dsadasd", "akkka", "brrrr"));
        DataMessage data = new DataMessage(usernew.getId(), chat.getId(), message.getId(), "dsad");

        try {
            messageService.updateMessage(data);
        } catch (MessageUpdateException e) {
            assertEquals(e.getMessage(), "Exception while updating message");
        }
    }

    @Test
    void deleteMessage_Pass() {
        Message messageDelete = new Message(user.getId(), chat.getId(), new Date(), "dsadsadsadsads");
        messageDelete = messageRepository.save(messageDelete);

        DataMessage data = new DataMessage(user.getId(), chat.getId(), messageDelete.getId(), "dsad");
        messageService.deleteMessage(data);

        assertFalse(messageRepository.existsById(messageDelete.getId()));
        assertFalse(chatRepository
                .findChatById(data.getIdChat())
                .getIdMessages()
                .contains(messageDelete.getId()));
    }

    @Test
    void deleteMessage_FailOne() {
        Message messageDelete = new Message(user.getId(), chat.getId(), new Date(), "dsadsadsadsads");
        messageDelete = messageRepository.save(messageDelete);

        DataMessage data = new DataMessage(user.getId(), chat.getId(), messageDelete.getId(), "dsad");

        try {
            messageService.deleteMessage(data);
        } catch (MessageDeleteException e) {
            assertEquals(e.getMessage(), "Only the author can delete message");
        }
    }

    @Test
    void deleteMessage_FailTwo() {
        Message messageDelete = new Message(user.getId(), chat.getId(), new Date(), "dsadsadsadsads");
        messageDelete = messageRepository.save(messageDelete);

        DataMessage data = new DataMessage(user.getId(), "", messageDelete.getId(), "dsad");

        try {
            messageService.deleteMessage(data);
        } catch (UpdateChatException e) {
            assertEquals(e.getMessage(), "There is no such chat");
        }
    }

}
