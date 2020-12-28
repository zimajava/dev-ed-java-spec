package org.zipli.socknet.service.ws.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.dto.MessageData;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.MessageRepository;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.service.ws.IMessageService;
import reactor.core.publisher.Sinks;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessageService implements IMessageService {
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final JwtUtils jwtUtils;
    private final Map<String, Sinks.Many<String>> messageEmitterByUserId = new ConcurrentHashMap<>();

    public MessageService(UserRepository userRepository, ChatRepository chatRepository, MessageRepository messageRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Message sendMessage(MessageData data) {

        Message message = new Message(data.getIdUser(), data.getIdChat(), new Date(), data.getTextMessage());
        message = messageRepository.save(message);

        Chat chat = chatRepository.findChatById(data.getIdChat());
        chat.getIdMessages().add(message.getId());
        chatRepository.save(chat);

        return message;
    }

    @Override
    public Chat createGroupChat(ChatData data) throws CreateChatException {

        if (!chatRepository.existsByChatName(data.getChatName())) {

            Chat chat = new Chat(data.getChatName(),
                    false,
                    null,
                    Collections.singletonList(data.getIdUser()),
                    data.getIdUser());

            chat = chatRepository.save(chat);

            User user = userRepository.getUserById(data.getIdUser());
            user.getChatsId().add(chat.getId());
            userRepository.save(user);

            return chat;
        } else {
            throw new CreateChatException("Such a chat already exists");
        }

    }

    @Override
    public Chat createPrivateChat(ChatData data) throws CreateChatException {

        if (!chatRepository.existsByChatName(data.getChatName())) {

            User creatorUser = userRepository.getUserById(data.getIdUser());
            User user = userRepository.getUserById(data.getSecondUserId());

            Chat chat = new Chat(data.getChatName(),
                    true,
                    data.getIdUser());

            chat.getIdUsers().add(creatorUser.getId());
            chat.getIdUsers().add(user.getId());
            chat = chatRepository.save(chat);

            creatorUser.getChatsId().add(chat.getId());
            user.getChatsId().add(chat.getId());

            List<User> users = new ArrayList<>();
            users.add(creatorUser);
            users.add(user);

            userRepository.saveAll(users);

            return chat;
        } else {
            throw new CreateChatException("Such a chat already exists");
        }
    }

    @Override
    public Chat updateChat(ChatData data) throws UpdateChatException {

        Chat chat = chatRepository.findChatById(data.getIdChat());

        if (chat != null) {
            chat.setChatName(data.getChatName());
            chat = chatRepository.save(chat);
            return chat;
        } else {
            throw new UpdateChatException("Change chat failed");
        }
    }

    @Override
    public void removeChat(ChatData data) throws RemoveChatException {

        Chat chat = chatRepository.getByChatNameAndCreatorUserId(data.getChatName(), data.getIdUser());

        if (chat != null) {

            Collection<String> listIdUsers = chat.getIdUsers();

            userRepository.saveAll(userRepository.findUsersByIdIn(listIdUsers).stream()
                    .map(user -> {
                        user.getChatsId().remove(data.getIdChat());
                        return user;
                    })
                    .collect(Collectors.toList()));
            messageRepository.deleteAllByChatId(data.getIdChat());
            chatRepository.deleteById(data.getIdChat());

        } else {
            throw new RemoveChatException("Only the creator can delete");
        }
    }

    @Override
    public Chat leaveChat(ChatData data) {

        Chat chat = chatRepository.findChatById(data.getIdChat());
        chat.getIdUsers().remove(data.getIdUser());
        chat = chatRepository.save(chat);

        User user = userRepository.getUserById(data.getIdUser());
        user.getChatsId().remove(chat.getId());
        userRepository.save(user);

        return chat;
    }

    @Override
    public Chat joinChat(ChatData data) {

        Chat chat = chatRepository.findChatById(data.getIdChat());
        List<String> listIdUsers = chat.getIdUsers();

        if (!listIdUsers.contains(data.getIdUser())) {

            User user = userRepository.getUserById(data.getIdUser());
            user.getChatsId().add(data.getIdChat());
            userRepository.save(user);

            listIdUsers.add(data.getIdUser());
            chat = chatRepository.save(chat);
        }

        return chat;
    }

    @Override
    public List<Message> getMessages(MessageData data) {

        Chat chat = chatRepository.findChatById(data.getIdChat());

        List<String> listIdMessages = chat.getIdMessages();
        List<Message> messages = new ArrayList<>();

        for (String idMessage : listIdMessages) {
            messages.add(messageRepository.getMessageById(idMessage));
        }

        return messages;
    }

    @Override
    public List<Chat> showChatsByUser(ChatData data) {

        User user = userRepository.getUserById(data.getIdUser());

        return chatRepository.getChatsByIdIn(user.getChatsId());
    }

    @Override
    public void addMessageEmitterByToken(String token, Sinks.Many<String> emitter) throws CreateSocketException {
        try {
            String username = jwtUtils.getUserNameFromJwtToken(token);
            User user = userRepository.findUserByUserName(username);
            messageEmitterByUserId.put(user.getId(), emitter);
        } catch (Exception e) {
            throw new CreateSocketException("Can't create connect to user, Exception cause: " + e.getMessage() + " on class " + e.getClass().getSimpleName());
        }
    }

    @Override
    public Message updateMessage(MessageData data) throws MessageUpdateException {

        Message message = messageRepository.getMessageById(data.getMessageId());

        if (message.getAuthorId().equals(data.getIdUser())) {
            message.setTextMessage(data.getTextMessage());
            message = messageRepository.save(message);

            return message;
        } else {
            throw new MessageUpdateException("Exception while updating message");
        }
    }

    @Override
    public void deleteMessage(MessageData data) throws MessageDeleteException, UpdateChatException {

        Message message = messageRepository.getMessageById(data.getMessageId());

        if (message.getAuthorId().equals(data.getIdUser())) {
            Chat chat = chatRepository.findChatById(data.getIdChat());
            if (chat != null) {
                chat.getIdMessages().remove(message.getId());
                chatRepository.save(chat);
            } else {
                throw new UpdateChatException("There is no such chat");
            }
            messageRepository.delete(message);
        } else {
            throw new MessageDeleteException("Only the author can delete message");
        }
    }
}
