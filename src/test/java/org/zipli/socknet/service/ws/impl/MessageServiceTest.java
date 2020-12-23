package org.zipli.socknet.service.ws.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.zipli.socknet.dto.Data;
import org.zipli.socknet.exception.CreateChatException;
import org.zipli.socknet.exception.MessageDeleteException;
import org.zipli.socknet.exception.MessageUpdateException;
import org.zipli.socknet.exception.RemoveChatException;
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
    private Data data;
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
        data = new Data("textMessage", user.getId(), chat.getId(), "", "ChatName");
        message = new Message(user.getId(), chat.getId(), new Date(), "dsadsadsadsads");
    }

    @Test
    void sendMessage() {

        Message message = messageService.sendMessage(data);

        assertEquals(data.getTextMessage(), message.getTextMessage());
        assertEquals(data.getUserId(), message.getAuthorId());
        assertEquals(data.getChatId(), message.getChatId());
    }

    @Test
    void createGroupChatPass() {

        Chat chat = messageService.createGroupChat(data);

        assertTrue(chatRepository.existsByChatName(chat.getChatName()));
        chatRepository.deleteAll();
    }

    @Test
    void createGroupChatFail() {

        try {
            Chat chatOne = messageService.createGroupChat(data);
            Chat chatTwo = messageService.createGroupChat(data);
        } catch (CreateChatException e) {
            assertEquals(e.getMessage(), "Such a chat already exists");
        }
        chatRepository.deleteAll();
    }

    @Test
    void createPrivateChatPass() {

        userRepository.save(new User("kkkk@gma.vv", "ghjk", "teaama", "morgen"));
        data.setUserName("teaama");
        Chat chat = messageService.createPrivateChat(data);

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

        Data dataTree = new Data("textMessage", userOne.getId(), chat.getId(), "", chat.getChatName());

        messageService.removeChat(dataTree);

        assertFalse(chatRepository.existsByChatName(chat.getChatName()));
        assertFalse(messageRepository.existsByChatId(chat.getId()));

        chatRepository.deleteAll();
    }

    @Test
    void removeChatFail() {

        Data dataTree = new Data("textMessage", "kakoitoId", chat.getId(), "", chat.getChatName());

        try {
            messageService.removeChat(dataTree);
        } catch (RemoveChatException e) {
            assertEquals(e.getMessage(), "Only the creator can delete");
        }
    }

    @Test
    void joinChat() {

        Chat chat = messageService.joinChat(data);
        User userUpdate = userRepository.getUserById(user.getId());

        assertTrue(chat.getIdUsers().contains(data.getUserId()));
        assertTrue(userUpdate.getChatsId().contains(chat.getId()));
    }

    @Test
    void updateChat() {

        Data data = new Data("textMessage", user.getId(), chat.getId(), "", "NewChatName");
        Chat chat = messageService.updateChat(data);

        assertEquals(chat.getChatName(), data.getNameChat());
    }

    @Test
    void showChatsByUser() {

        List<Chat> chats = messageService.showChatsByUser(data);

        assertEquals(user.getChatsId().size(), chats.size());
    }

    @Test
    void leaveChat() {

        Chat chat = new Chat("", true, user.getId());
        chat.getIdUsers().add(user.getId());
        chat = chatRepository.save(chat);

        data.setChatId(chat.getId());

        Chat newChat = messageService.leaveChat(data);

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

        data.setChatId(chat.getId());
        List<Message> messages = messageService.getMessages(data);

        assertEquals(messages.size(), 3);

        assertEquals(messages.get(0).getMessage(), messageOne.getMessage());
        assertEquals(messages.get(1).getMessage(), messageTwo.getMessage());
        assertEquals(messages.get(2).getMessage(), messageTree.getMessage());

    }

    @Test
    void updateMessagePass() {

        message = messageRepository.save(message);

        Data data = new Data("newMessage", user.getId(), chat.getId(), message.getId(), user.getUserName(), chat.getChatName());
        Message updateMessage = messageService.updateMessage(data);

        assertEquals(updateMessage.getTextMessage(), data.getTextMessage());
        assertEquals(updateMessage.getId(), data.getMessageId());
    }

    @Test
    void updateMessageFail() {

        message = messageRepository.save(message);

        User usernew = userRepository.save(new User("akkka@fsa.cas","dsadasd","akkka","brrrr"));
        Data data = new Data("newMessage", usernew.getId(), chat.getId(), message.getId(), user.getUserName(), chat.getChatName());

        try {
            messageService.updateMessage(data);
        } catch (MessageUpdateException e) {
            assertEquals(e.getMessage(), "Exception while updating message");
        }
    }

    @Test
    void DeleteMessagePass() {
        Message messageDelete = new Message(user.getId(), chat.getId(), new Date(), "dsadsadsadsads");
        messageDelete = messageRepository.save(messageDelete);

        Data data = new Data("newMessage", user.getId(), chat.getId(), messageDelete.getId(), user.getUserName(), chat.getChatName());
        messageService.deleteMessage(data);

        assertFalse(messageRepository.existsById(messageDelete.getId()));
        assertFalse(chatRepository
                .findChatById(data.getChatId())
                .getIdMessages()
                .contains(messageDelete.getId()));
    }

    @Test
    void DeleteMessageFail() {
        Message messageDelete = new Message(user.getId(), chat.getId(), new Date(), "dsadsadsadsads");
        messageDelete = messageRepository.save(messageDelete);

        Data data = new Data("newMessage", "", chat.getId(), messageDelete.getId(), user.getUserName(), chat.getChatName());

        try {
            messageService.deleteMessage(data);
        } catch (MessageDeleteException e) {
            assertEquals(e.getMessage(), "Exception while delete message");
        }
    }

}
