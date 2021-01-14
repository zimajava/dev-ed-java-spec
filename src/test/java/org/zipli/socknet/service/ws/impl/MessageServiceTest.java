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
import org.zipli.socknet.exception.chat.ChatNotFoundException;
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
    private MessageService messageService;
    private Message message;

    @Autowired
    UserRepository userRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    ChatRepository chatRepository;

    EmitterService emitterService = new EmitterService(userRepository,new JwtUtils());

    @BeforeEach
    void setUp() {

        messageService = new MessageService(chatRepository, messageRepository, emitterService);
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
            assertEquals(e.getMessage(), "Only the author can update message {}");
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
        } catch (ChatNotFoundException e) {
            assertEquals(e.getMessage(), "Chat {} doesn't exist");
        }
    }


}
