package org.zipli.socknet.service.ws.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.WsMessage;
import org.zipli.socknet.exception.CreateChatException;
import org.zipli.socknet.exception.RemoveChatException;
import org.zipli.socknet.exception.UpdateChatException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.MessageRepository;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.service.ws.IMessagerService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessagerService implements IMessagerService {

    final UserRepository userRepository;

    final ChatRepository chatRepository;

    final MessageRepository messageRepository;

    public MessagerService(UserRepository userRepository, ChatRepository chatRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Message sendMessage(WsMessage wsMessage) {

        Message message = new Message(wsMessage.getUserId(), wsMessage.getChatId(), new Date(), wsMessage.getTextMessage());
        message = messageRepository.save(message);

        Chat chat = chatRepository.findChatById(wsMessage.getChatId());
        chat.getIdMessages().add(message.getId());
        chatRepository.save(chat);

        return message;
    }

    @Override
    public Chat createGroupChat(WsMessage wsMessage) throws CreateChatException {

        if (!chatRepository.existsByChatName(wsMessage.getNameChat())) {

            Chat chat = new Chat(wsMessage.getNameChat(),
                    false,
                    null,
                    Collections.singletonList(wsMessage.getUserId()),
                    wsMessage.getUserId());

            chat = chatRepository.save(chat);

            User user = userRepository.getUserById(wsMessage.getUserId());
            user.getChatsId().add(chat.getId());
            userRepository.save(user);

            return chat;
        } else {
            throw new CreateChatException("Such a chat already exists");
        }

    }

    @Override
    public Chat createPrivateChat(WsMessage wsMessage) throws CreateChatException {

        if (!chatRepository.existsByChatName(wsMessage.getNameChat())) {

            User userOne = userRepository.getUserById(wsMessage.getUserId());
            User userTwo = userRepository.getByUserName(wsMessage.getUserName());

            Chat chat = new Chat(wsMessage.getNameChat(),
                    true,
                    wsMessage.getUserId());


            List<String> usersId = new ArrayList<>();
            usersId.add(userOne.getId());
            usersId.add(userTwo.getId());

            chat.getIdUsers().addAll(usersId);
            chat = chatRepository.save(chat);

            userOne.getChatsId().add(chat.getId());
            userTwo.getChatsId().add(chat.getId());

            List<User> users = new ArrayList<>();
            users.add(userOne);
            users.add(userTwo);

            userRepository.saveAll(users);

            return chat;
        } else {
            throw new CreateChatException("Such a chat already exists");
        }
    }

    @Override
    public void removeChat(WsMessage wsMessage) throws RemoveChatException {

        Chat chat = chatRepository.getByChatNameAndCreatorUserId(wsMessage.getNameChat(), wsMessage.getUserId());

        if (chat != null) {

            Collection<String> listIdUsers = chat.getIdUsers();

            userRepository.saveAll(userRepository.findUsersByIdIn(listIdUsers).stream()
                    .peek(user -> user.getChatsId().remove(wsMessage.getChatId()))
                    .collect(Collectors.toList()));
            messageRepository.deleteAllByChatId(wsMessage.getChatId());
            chatRepository.deleteById(wsMessage.getChatId());

        } else {
            throw new RemoveChatException("Only the creator can delete");
        }
    }

    @Override
    public Chat leaveChat(WsMessage wsMessage) {

        Chat chat = chatRepository.findChatById(wsMessage.getChatId());
        chat.getIdUsers().remove(wsMessage.getUserId());
        chat = chatRepository.save(chat);

        User user = userRepository.getUserById(wsMessage.getUserId());
        user.getChatsId().remove(chat.getId());
        userRepository.save(user);

        return chat;
    }

    @Override
    public Chat joinChat(WsMessage wsMessage) {

        Chat chat = chatRepository.findChatById(wsMessage.getChatId());
        List<String> listIdUsers = chat.getIdUsers();

        if (!listIdUsers.contains(wsMessage.getUserId())) {

            User user = userRepository.getUserById(wsMessage.getUserId());
            user.getChatsId().add(wsMessage.getChatId());
            userRepository.save(user);

            listIdUsers.add(wsMessage.getUserId());
            chat = chatRepository.save(chat);
        }

        return chat;
    }

    @Override
    public List<Message> getMessages(WsMessage wsMessage) {

        Chat chat = chatRepository.findChatById(wsMessage.getChatId());

        List<String> listIdMessages = chat.getIdMessages();
        List<Message> messages = new ArrayList<>();

        for (String idMessage : listIdMessages) {
            messages.add(messageRepository.getMessageById(idMessage));
        }

        return messages;
    }

    @Override
    public Chat updateChat(WsMessage wsMessage) {

        if (!chatRepository.existsByChatNameAndCreatorUserId(wsMessage.getNameChat(), wsMessage.getUserId())) {

            Chat chat = chatRepository.findChatById(wsMessage.getChatId());
            chat.setChatName(wsMessage.getNameChat());
            chat = chatRepository.save(chat);

            return chat;
        } else {
            throw new UpdateChatException("Change chat failed");
        }
    }

    @Override
    public List<Chat> showChatsByUser(WsMessage wsMessage) {

        User user = userRepository.getUserById(wsMessage.getUserId());

        return chatRepository.getChatsByIdIn(user.getChatsId());
    }

}
