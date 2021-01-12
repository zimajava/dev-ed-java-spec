package org.zipli.socknet.service.ws.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.dto.Command;
import org.zipli.socknet.dto.MessageData;
import org.zipli.socknet.dto.WsMessage;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.MessageRepository;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.service.ws.IMessageService;
import org.zipli.socknet.util.JsonUtils;
import reactor.core.publisher.Sinks;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageService implements IMessageService {
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final JwtUtils jwtUtils;
    private final Map<String, List<Sinks.Many<String>>> messageEmitterByUserId = new ConcurrentHashMap<>();

    public MessageService(UserRepository userRepository, ChatRepository chatRepository, MessageRepository messageRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Message sendMessage(MessageData data) {

        Message message = new Message(data.getIdUser(), data.getIdChat(), new Date(), data.getTextMessage());
        final Message finalMessage = messageRepository.save(message);
        Chat chat = chatRepository.findChatById(data.getIdChat());
        chat.getIdMessages().add(message.getId());

        chat.getIdUsers().parallelStream()
                .forEach(userId -> sendMessageToUser(userId,
                        new WsMessage(Command.MESSAGE_SEND,
                                new MessageData(userId,
                                        chat.getId(),
                                        finalMessage.getId(),
                                        finalMessage.getTextMessage()
                                )
                        ))
                );

        chatRepository.save(chat);

        return message;
    }

    @Override
    public Chat createGroupChat(ChatData data) throws CreateChatException {

        if (!chatRepository.existsByChatName(data.getChatName())) {

            Chat chat = new Chat(data.getChatName(),
                    false,
                    new ArrayList<>(),
                    Collections.singletonList(data.getIdUser()),
                    null,
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
            final Chat finalChat = chatRepository.save(chat);

            creatorUser.getChatsId().add(chat.getId());
            user.getChatsId().add(chat.getId());

            List<User> users = new ArrayList<>();
            users.add(creatorUser);
            users.add(user);

            userRepository.saveAll(users);

            finalChat.getIdUsers().parallelStream()
                    .forEach(userId -> sendMessageToUser(userId,
                            new WsMessage(Command.CHAT_JOIN,
                                    new ChatData(userId,
                                            finalChat.getId(),
                                            finalChat.getChatName()
                                    )
                            ))
                    );

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
            final Chat finalChat = chatRepository.save(chat);

            finalChat.getIdUsers().parallelStream()
                    .forEach(userId -> sendMessageToUser(userId,
                            new WsMessage(Command.CHAT_UPDATE,
                                    new ChatData(userId,
                                            finalChat.getId(),
                                            finalChat.getChatName()
                                    )
                            ))
                    );

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

            chat.getIdUsers().parallelStream()
                    .forEach(userId -> sendMessageToUser(userId,
                            new WsMessage(Command.CHAT_DELETE,
                                    new ChatData(userId,
                                            chat.getId(),
                                            chat.getChatName()
                                    )
                            ))
                    );

        } else {
            throw new RemoveChatException("Only the creator can delete");
        }
    }

    @Override
    public Chat leaveChat(ChatData data) {

        Chat chat = chatRepository.findChatById(data.getIdChat());
        chat.getIdUsers().remove(data.getIdUser());
        final Chat finalChat = chatRepository.save(chat);

        User user = userRepository.getUserById(data.getIdUser());
        user.getChatsId().remove(chat.getId());
        userRepository.save(user);

        finalChat.getIdUsers().parallelStream()
                .forEach(userId -> sendMessageToUser(userId,
                        new WsMessage(Command.CHAT_LEAVE,
                                new ChatData(userId,
                                        finalChat.getId(),
                                        finalChat.getChatName(),
                                        data.getIdUser()
                                )
                        ))
                );

        return chat;
    }

    @Override
    public Chat joinChat(ChatData data) {

        Chat chat = chatRepository.findChatById(data.getIdChat());
        List<String> listIdUsers = chat.getIdUsers();

        if (!chat.isPrivate() && !listIdUsers.contains(data.getIdUser())) {

            User user = userRepository.getUserById(data.getIdUser());
            user.getChatsId().add(data.getIdChat());
            userRepository.save(user);

            listIdUsers.add(data.getIdUser());
            final Chat finalChat = chatRepository.save(chat);

            finalChat.getIdUsers().parallelStream()
                    .forEach(userId -> sendMessageToUser(userId,
                            new WsMessage(Command.CHAT_JOIN,
                                    new ChatData(userId,
                                            finalChat.getId(),
                                            finalChat.getChatName(),
                                            data.getIdUser()
                                    )
                            ))
                    );
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
    public String addMessageEmitterByToken(String token, Sinks.Many<String> emitter) throws CreateSocketException {
        try {
            String username = jwtUtils.getUserNameFromJwtToken(token);
            User user = userRepository.findUserByUserName(username);
            String userId = user.getId();
            messageEmitterByUserId.computeIfAbsent(userId, e -> new CopyOnWriteArrayList<>()).add(emitter);
            return userId;
        } catch (Exception e) {
            throw new CreateSocketException("Can't create connect to user, Exception cause: " + e.getMessage() + " on class " + e.getClass().getSimpleName());
        }
    }

    @Override
    public Message updateMessage(MessageData data) throws MessageUpdateException {

        Message message = messageRepository.getMessageById(data.getMessageId());

        if (message.getAuthorId().equals(data.getIdUser())) {
            message.setTextMessage(data.getTextMessage());
            final Message finalMessage = messageRepository.save(message);
            final Chat finalChat = chatRepository.findChatById(data.getIdChat());

            finalChat.getIdUsers().parallelStream()
                    .forEach(userId -> sendMessageToUser(userId,
                            new WsMessage(Command.MESSAGE_UPDATE,
                                    new MessageData(userId,
                                            finalChat.getId(),
                                            finalMessage.getId(),
                                            finalMessage.getTextMessage()
                                    )
                            ))
                    );

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
                final Chat finalChat = chatRepository.save(chat);

                finalChat.getIdUsers().parallelStream()
                        .forEach(userId -> sendMessageToUser(userId,
                                new WsMessage(Command.MESSAGE_DELETE,
                                        new MessageData(userId,
                                                finalChat.getId(),
                                                message.getId(),
                                                message.getTextMessage()
                                        )
                                ))
                        );
            } else {
                throw new UpdateChatException("There is no such chat");
            }
            messageRepository.delete(message);
        } else {
            throw new MessageDeleteException("Only the author can delete message");
        }
    }

    void sendMessageToUser(String userId, WsMessage wsMessage) {
        List<Sinks.Many<String>> emittersByUser = messageEmitterByUserId.get(userId);
        if (emittersByUser != null) {
            emittersByUser.forEach(emitter -> emitter.tryEmitNext(JsonUtils.jsonWriteHandle(wsMessage)));
        } else {
            if (wsMessage.getCommand().equals(Command.CHAT_LEAVE) || wsMessage.getCommand().equals(Command.CHAT_JOIN)) {
                log.info("User = {userId: {} isn't online: {}, user: {} not sent.}", userId, wsMessage.getCommand(), wsMessage.getData().getIdUser());
            } else {
                log.info("User = {userId: {} isn't online: {} not sent.}", userId, wsMessage.getCommand());
            }
        }
    }

    @Override
    public void deleteMessageEmitterByUserId(String userId, Sinks.Many<String> emitter) throws DeleteSessionException {
        try {
            messageEmitterByUserId.remove(userId);
        } catch (Exception e) {
            throw new DeleteSessionException("Can't delete message emitter");
        }
    }
}
