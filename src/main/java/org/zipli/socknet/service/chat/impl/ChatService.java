package org.zipli.socknet.service.chat.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.BaseData;
import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.dto.Command;
import org.zipli.socknet.dto.FullChatData;
import org.zipli.socknet.dto.response.WsMessageResponse;
import org.zipli.socknet.exception.ErrorStatusCode;
import org.zipli.socknet.exception.auth.UserNotFoundException;
import org.zipli.socknet.exception.chat.*;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.MessageRepository;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.repository.model.Chat;
import org.zipli.socknet.repository.model.User;
import org.zipli.socknet.service.chat.IChatService;
import org.zipli.socknet.service.chat.IEmitterService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatService implements IChatService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final IEmitterService emitterService;

    public ChatService(UserRepository userRepository, ChatRepository chatRepository, MessageRepository messageRepository, EmitterService emitterService) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.emitterService = emitterService;
    }

    public Chat createChat(FullChatData data) throws CreateChatException, UserNotFoundException {

        if (!chatRepository.existsByChatName(data.getChatName())) {

            User userCreator = userRepository.getUserById(data.getUserId());
            if (userCreator == null) {
                throw new UserNotFoundException(ErrorStatusCode.USER_DOES_NOT_EXIST);
            }

            Chat chat = new Chat(data.getChatName(), data.isPrivate(),
                    data.getChatParticipants(),
                    data.getUserId());
            chat.getUsersId().add(data.getUserId());
            chatRepository.save(chat);

            userCreator.getChatsId().add(chat.getId());
            userRepository.save(userCreator);

            for (String userInGroup : data.getChatParticipants()) {
                User user = userRepository.getUserById(userInGroup);
                if (user != null) {
                    user.getChatsId().add(chat.getId());
                    userRepository.save(user);
                } else {
                    log.error("User {} does not exist!", userInGroup);
                }
            }

            log.info("Chat {} successfully created id {} user {} ", data.getChatName(), chat.getId(), data.getUserId());

            chat.getUsersId().parallelStream()
                    .forEach(userId -> emitterService.sendMessageToUser(userId,
                            new WsMessageResponse(Command.CHAT_USER_ADD,
                                    new FullChatData(data.getUserId(),
                                            chat.getId(),
                                            chat.getChatName(),
                                            chat.getUsersId(),
                                            chat.isPrivate()
                                    )
                            ))
                    );

            return chat;
        } else {
            throw new CreateChatException(ErrorStatusCode.CHAT_ALREADY_EXISTS);
        }

    }

    @Override
    public Chat updateChat(FullChatData data) throws UpdateChatException {

        Chat chat = chatRepository.findChatById(data.getChatId());
        if (chat != null) {
            if (chat.getCreatorUserId().equals(data.getUserId())) {
                chat.setChatName(data.getChatName());
                final Chat finalChat = chatRepository.save(chat);

                finalChat.getUsersId().parallelStream()
                        .forEach(userId -> emitterService.sendMessageToUser(userId,
                                new WsMessageResponse(Command.CHAT_UPDATE,
                                        new FullChatData(data.getUserId(),
                                                finalChat.getId(),
                                                finalChat.getChatName(),
                                                finalChat.getUsersId(),
                                                finalChat.isPrivate()
                                        )
                                ))
                        );
                log.info("Chat {} update, his name {} user {}", data.getChatId(), data.getChatName(), data.getUserId());

                return chat;
            } else {
                throw new UpdateChatException(ErrorStatusCode.CHAT_ACCESS_ERROR);
            }
        } else {
            throw new UpdateChatException(ErrorStatusCode.CHAT_NOT_EXISTS);
        }
    }

    @Override
    public void deleteChat(ChatData data) throws DeleteChatException {

        Chat chat = chatRepository.findChatById(data.getChatId());
        if (chat != null) {
            if (chat.getCreatorUserId().equals(data.getUserId())) {

                userRepository.saveAll(
                        userRepository.findUsersByIdIn(
                                chat.getUsersId()).stream()
                                .map(user -> {
                                    user.getChatsId().remove(data.getChatId());
                                    return user;
                                })
                                .collect(Collectors.toList())
                );

                messageRepository.deleteAllByChatId(data.getChatId());
                chatRepository.deleteById(data.getChatId());

                log.info("Chat {} name {} delete by user {} ", data.getChatId(), data.getUserId(), chat.getChatName());

                chat.getUsersId().parallelStream()
                        .forEach(userId -> emitterService.sendMessageToUser(userId,
                                new WsMessageResponse(Command.CHAT_DELETE,
                                        new FullChatData(data.getUserId(),
                                                chat.getId(),
                                                chat.getChatName(),
                                                chat.getUsersId(),
                                                chat.isPrivate()
                                        )
                                ))
                        );
            } else {
                throw new DeleteChatException(ErrorStatusCode.CHAT_ACCESS_ERROR);
            }
        } else {
            throw new DeleteChatException(ErrorStatusCode.CHAT_NOT_EXISTS);
        }
    }

    @Override
    public Chat leaveChat(ChatData data) {

        Chat chat = chatRepository.findChatById(data.getChatId());
        if (chat != null) {
            chat.getUsersId().remove(data.getUserId());
            final Chat finalChat = chatRepository.save(chat);

            User user = userRepository.getUserById(data.getUserId());
            user.getChatsId().remove(chat.getId());
            userRepository.save(user);

            log.info("Leave chat {} user {}", data.getChatId(), data.getUserId());

            finalChat.getUsersId().parallelStream()
                    .forEach(userId -> emitterService.sendMessageToUser(userId,
                            new WsMessageResponse(Command.CHAT_LEAVE,
                                    new FullChatData(data.getUserId(),
                                            finalChat.getId(),
                                            finalChat.getChatName(),
                                            finalChat.getUsersId(),
                                            finalChat.isPrivate()
                                    )
                            ))
                    );

            return chat;
        } else {
            throw new LeaveChatException(ErrorStatusCode.CHAT_NOT_EXISTS);
        }
    }

    @Override
    public Chat joinChat(ChatData data) {

        Chat chat = chatRepository.findChatById(data.getChatId());

        if (chat != null) {
            List<String> chatUsers = chat.getUsersId();
            if (!chat.isPrivate() && !chatUsers.contains(data.getUserId())) {

                User user = userRepository.getUserById(data.getUserId());
                user.getChatsId().add(data.getChatId());
                userRepository.save(user);

                chatUsers.add(data.getUserId());
                final Chat finalChat = chatRepository.save(chat);

                log.info("Join Chat user {} to chat {}", data.getUserId(), finalChat.getId());

                finalChat.getUsersId().parallelStream()
                        .forEach(userId -> emitterService.sendMessageToUser(userId,
                                new WsMessageResponse(Command.CHAT_USER_ADD,
                                        new FullChatData(data.getUserId(),
                                                finalChat.getId(),
                                                finalChat.getChatName(),
                                                finalChat.getUsersId(),
                                                finalChat.isPrivate()
                                        )
                                ))
                        );
            } else {
                throw new JoinChatException(ErrorStatusCode.CHAT_ACCESS_ERROR);
            }
        } else {
            throw new JoinChatException(ErrorStatusCode.CHAT_NOT_EXISTS);
        }
        return chat;
    }

    @Override
    public List<Chat> showChatsByUser(BaseData data) throws UserNotFoundException {

        User user = userRepository.getUserById(data.getUserId());
        if (user != null) {
            log.info("Get Chats By User {}", data.getUserId());
            return chatRepository.getChatsByIdIn(user.getChatsId());
        } else {
            throw new UserNotFoundException(ErrorStatusCode.USER_DOES_NOT_EXIST);
        }
    }

}
