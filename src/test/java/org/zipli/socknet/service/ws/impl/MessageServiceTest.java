package org.zipli.socknet.service.ws.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.dto.MessageData;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.exception.chat.CreateChatException;
import org.zipli.socknet.exception.chat.DeleteChatException;
import org.zipli.socknet.exception.chat.UpdateChatException;
import org.zipli.socknet.exception.message.MessageDeleteException;
import org.zipli.socknet.exception.message.MessageUpdateException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.MessageRepository;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import reactor.core.publisher.Sinks;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataMongoTest
class MessageServiceTest {

    private User user;
    private Chat chat;
    private MessageData messageData;
    private ChatData dataChat;
    private MessageService messageService;
    private Message message;

    @Autowired
    UserRepository userRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    ChatRepository chatRepository;

    @MockBean
    Sinks.Many<String> emitter;

    @MockBean
    Map<String, Sinks.Many<String>> messageEmitterByUserId;

    @MockBean
    MessageService messageService1;

    @BeforeEach
    void setUp() {

        messageService = new MessageService(userRepository, chatRepository, messageRepository, new JwtUtils());
        user = new User("Email@com", "password", "Username", "MoiNik");
        user = userRepository.save(user);

        chat = new Chat("NameGroupChat", false,new ArrayList<>(),Collections.singletonList(user.getId()), user.getId());
        chat = chatRepository.save(chat);
        log.info(String.valueOf(chat));
        messageData = new MessageData(
                user.getId(),
                chat.getId(),
                "dsadsda",
                "");
        dataChat = new ChatData(user.getId(),
                chat.getId(),
                "vgtunj");
        message = new Message(user.getId(), chat.getId(), new Date(), "dsadsadsadsads");
    }

    @Test
    void sendMessage() {

        Message message = messageService.sendMessage(messageData);

        assertEquals(messageData.getTextMessage(), message.getTextMessage());
        assertEquals(messageData.getIdUser(), message.getAuthorId());
        assertEquals(messageData.getIdChat(), message.getChatId());
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
        new ChatData(user.getId(),
                chat.getId(),
                chat.getChatName());
        ChatData dataTree = new ChatData(userOne.getId(), chat.getId(), chat.getChatName());

        messageService.deleteChat(dataTree);

        assertFalse(chatRepository.existsByChatName(chat.getChatName()));
        assertFalse(messageRepository.existsByChatId(chat.getId()));

        chatRepository.deleteAll();
    }

    @Test
    void removeChat_Fail() {

        ChatData dataTree = new ChatData("kakoitoId", chat.getId(), chat.getChatName());

        try {
            messageService.deleteChat(dataTree);
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
        Chat chat = messageService.joinChat(dataChat);
        User userUpdate = userRepository.getUserById(user.getId());

        assertTrue(chat.getCreatorUserId().contains(dataChat.getIdUser()));
        assertTrue(userUpdate.getChatsId().contains(chat.getId()));
    }

    @Test
    void updateChat() {

        ChatData chatData = new ChatData(user.getId(), chat.getId(), "NewChatName");
        Chat chat = messageService.updateChat(chatData);

        assertEquals(chat.getChatName(), chatData.getChatName());
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

        messageData.setIdChat(chat.getId());
        List<Message> messages = messageService.getMessages(messageData);

        assertEquals(messages.size(), 3);

        assertEquals(messages.get(0).getMessage(), messageOne.getMessage());
        assertEquals(messages.get(1).getMessage(), messageTwo.getMessage());
        assertEquals(messages.get(2).getMessage(), messageTree.getMessage());

    }

    @Test
    void updateMessage_Pass() {

        message = messageRepository.save(message);

        MessageData data = new MessageData(user.getId(), chat.getId(), message.getId(), "dsad");
        Message updateMessage = messageService.updateMessage(data);

        assertEquals(updateMessage.getTextMessage(), data.getTextMessage());
        assertEquals(updateMessage.getId(), data.getMessageId());
    }

    @Test
    void updateMessage_Fail() {

        message = messageRepository.save(message);

        User usernew = userRepository.save(new User("akkka@fsa.cas", "dsadasd", "akkka", "brrrr"));
        MessageData data = new MessageData(usernew.getId(), chat.getId(), message.getId(), "dsad");

        try {
            messageService.updateMessage(data);
        } catch (MessageUpdateException e) {
            assertEquals(e.getMessage(), "Only the author can update message");
        }
    }

    @Test
    void deleteMessage_Pass() {
        Message messageDelete = new Message(user.getId(), chat.getId(), new Date(), "dsadsadsadsads");
        messageDelete = messageRepository.save(messageDelete);

        MessageData data = new MessageData(user.getId(), chat.getId(), messageDelete.getId(), "dsad");
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

        MessageData data = new MessageData(user.getId(), chat.getId(), messageDelete.getId(), "dsad");

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

        MessageData data = new MessageData(user.getId(), "", messageDelete.getId(), "dsad");

        try {
            messageService.deleteMessage(data);
        } catch (UpdateChatException e) {
            assertEquals(e.getMessage(), "Chat doesn't exist");
        }
    }

    @Test
    void deleteMessageEmitterByUserId_Pass() {
        String token = "jbkug";
        String userId = new String();
        try {
            userId = Mockito.doReturn(new String())
                    .when(messageService1)
                    .addMessageEmitterByToken(token, emitter);
        } catch (CreateSocketException e) {
            e.printStackTrace();
        }

        try {
            messageService1.deleteMessageEmitterByUserId(userId, emitter);
        } catch (DeleteSessionException e) {
            e.printStackTrace();
        }

        assertFalse(messageEmitterByUserId.containsValue(userId));
    }

    @Test
    void deleteMessageEmitterByUserId_Fail() {
        String userId = "hgjfby";

        try {
            messageService.deleteMessageEmitterByUserId(userId, emitter);
        } catch (DeleteSessionException e) {
            assertEquals(e.getMessage(), "Can't delete message emitter");
        }
    }
}
