package org.zipli.socknet.service.ws.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.*;
import org.zipli.socknet.exception.ErrorStatusCode;
import org.zipli.socknet.exception.WsException;
import org.zipli.socknet.exception.auth.UserNotFoundException;
import org.zipli.socknet.exception.chat.*;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.MessageRepository;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.service.ws.IChatService;
import org.zipli.socknet.service.ws.IEmitterService;

import java.util.Collection;
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
            chat.getIdUsers().add(data.getUserId());
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

            log.info("GroupChat {} successfully created", data.getChatName());

            chat.getIdUsers().parallelStream()
                    .forEach(userId -> emitterService.sendMessageToUser(userId,
                            new WsMessageResponse(Command.CHAT_USER_ADD,
                                    new FullChatData(data.getUserId(),
                                            chat.getId(),
                                            chat.getChatName(),
                                            chat.getIdUsers(),
                                            chat.isPrivate()
                                    )
                            ))
                    );
            return chat;
        } else {
            throw new CreateChatException("Such a chat {} already exists");
        }

    }

    @Override
    public Chat updateChat(FullChatData data) throws UpdateChatException {

        Chat chat = chatRepository.findChatById(data.getChatId());
        if (chat != null) {
            if (chat.getCreatorUserId().equals(data.getUserId())) {
                chat.setChatName(data.getChatName());
                final Chat finalChat = chatRepository.save(chat);

                finalChat.getIdUsers().parallelStream()
                        .forEach(userId -> emitterService.sendMessageToUser(userId,
                                new WsMessageResponse(Command.CHAT_UPDATE,
                                        new FullChatData(data.getUserId(),
                                                finalChat.getId(),
                                                finalChat.getChatName(),
                                                finalChat.getIdUsers(),
                                                finalChat.isPrivate()
                                        )
                                ))
                        );
                return chat;
            } else {
                throw new UpdateChatException("Only the author can update chat {}",
                        WsException.CHAT_ACCESS_ERROR
                );
            }
        } else {
            throw new UpdateChatException("Chat {} doesn't exist",
                    WsException.CHAT_NOT_EXISTS
            );
        }
    }

    @Override
    public void deleteChat(ChatData data) throws DeleteChatException {

        Chat chat = chatRepository.findChatById(data.getChatId());
        if (chat != null) {
            if (chat.getCreatorUserId().equals(data.getUserId())) {
                Collection<String> listIdUsers = chat.getIdUsers();

                userRepository.saveAll(userRepository.findUsersByIdIn(listIdUsers).stream()
                        .map(user -> {
                            user.getChatsId().remove(data.getChatId());
                            return user;
                        })
                        .collect(Collectors.toList()));
                messageRepository.deleteAllByChatId(data.getChatId());
                chatRepository.deleteById(data.getChatId());

                chat.getIdUsers().parallelStream()
                        .forEach(userId -> emitterService.sendMessageToUser(userId,
                                new WsMessageResponse(Command.CHAT_DELETE,
                                        new FullChatData(data.getUserId(),
                                                chat.getId(),
                                                chat.getChatName(),
                                                chat.getIdUsers(),
                                                chat.isPrivate()
                                        )
                                ))
                        );
            } else {
                throw new DeleteChatException("Only the author can delete chat {}",
                        WsException.CHAT_ACCESS_ERROR.getNumberException()
                );
            }
        } else {
            throw new DeleteChatException("Chat {} doesn't exist",
                    WsException.CHAT_NOT_EXISTS.getNumberException()
            );
        }
    }

    @Override
    public Chat leaveChat(ChatData data) {

        Chat chat = chatRepository.findChatById(data.getChatId());
        if (chat != null) {
            chat.getIdUsers().remove(data.getUserId());
            final Chat finalChat = chatRepository.save(chat);

            User user = userRepository.getUserById(data.getUserId());
            user.getChatsId().remove(chat.getId());
            userRepository.save(user);

            finalChat.getIdUsers().parallelStream()
                    .forEach(userId -> emitterService.sendMessageToUser(userId,
                            new WsMessageResponse(Command.CHAT_LEAVE,
                                    new FullChatData(data.getUserId(),
                                            finalChat.getId(),
                                            finalChat.getChatName(),
                                            finalChat.getIdUsers(),
                                            finalChat.isPrivate()
                                    )
                            ))
                    );

            return chat;
        } else {
            throw new LeaveChatException("Chat {} doesn't exist");
        }
    }

    @Override
    public Chat joinChat(ChatData data) {

        Chat chat = chatRepository.findChatById(data.getChatId());

        if (chat != null) {
            List<String> listIdUsers = chat.getIdUsers();
            if (!chat.isPrivate() && !listIdUsers.contains(data.getUserId())) {

                User user = userRepository.getUserById(data.getUserId());
                user.getChatsId().add(data.getChatId());
                userRepository.save(user);
                log.info(String.valueOf(listIdUsers));
                listIdUsers.add(data.getUserId());
                final Chat finalChat = chatRepository.save(chat);
                log.info(String.valueOf(finalChat.getIdUsers()));
                finalChat.getIdUsers().parallelStream()
                        .forEach(userId -> emitterService.sendMessageToUser(userId,
                                new WsMessageResponse(Command.CHAT_USER_ADD,
                                        new FullChatData(data.getUserId(),
                                                finalChat.getId(),
                                                finalChat.getChatName(),
                                                finalChat.getIdUsers(),
                                                finalChat.isPrivate()
                                        )
                                ))
                        );
            } else {
                throw new JoinChatException("Can't access chat {}",
                        WsException.CHAT_ACCESS_ERROR.getNumberException()
                );
            }
        } else {
            throw new JoinChatException("Chat {} doesn't exist",
                    WsException.CHAT_NOT_EXISTS.getNumberException()
            );
        }

        return chat;
    }

    @Override
    public List<Chat> showChatsByUser(BaseData data) throws UserNotFoundException {

        User user = userRepository.getUserById(data.getUserId());
        if (user != null) {
            return chatRepository.getChatsByIdIn(user.getChatsId());
        } else {
            throw new UserNotFoundException(ErrorStatusCode.USER_DOES_NOT_EXIST);
        }
    }

}
