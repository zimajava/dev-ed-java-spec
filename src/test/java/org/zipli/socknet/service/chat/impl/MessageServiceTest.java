package org.zipli.socknet.service.chat.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zipli.socknet.dto.MessageData;
import org.zipli.socknet.exception.chat.ChatNotFoundException;
import org.zipli.socknet.exception.message.MessageDeleteException;
import org.zipli.socknet.exception.message.MessageUpdateException;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.MessageRepository;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.repository.model.Chat;
import org.zipli.socknet.repository.model.Message;
import org.zipli.socknet.repository.model.User;
import org.zipli.socknet.security.jwt.JwtUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Slf4j
@SpringBootTest
class MessageServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    ChatRepository chatRepository;
    EmitterService emitterService = new EmitterService(userRepository, new JwtUtils());
    private User user;
    private Chat chat;
    private MessageData messageData;
    private MessageService messageService;
    private Message message;

    @BeforeEach
    void setUp() {

        messageService = new MessageService(chatRepository, messageRepository, emitterService);
        user = new User("Email@com", "password", "Username", "MoiNik");
        user = userRepository.save(user);

        chat = new Chat("NameGroupChat", false, Collections.singletonList(user.getId()), user.getId());
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
        assertEquals(messageData.getUserId(), message.getAuthorId());
        assertEquals(messageData.getChatId(), message.getChatId());
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

        chat.getMessagesId().add(messageOne.getId());
        chat.getMessagesId().add(messageTwo.getId());
        chat.getMessagesId().add(messageTree.getId());

        chat = chatRepository.save(chat);

        messageData.setChatId(chat.getId());
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
                .findChatById(data.getChatId())
                .getMessagesId()
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
