package org.zipli.socknet.service.ws.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.dto.ChatGroupData;
import org.zipli.socknet.dto.Command;
import org.zipli.socknet.dto.WsMessageResponse;
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

import java.util.ArrayList;
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

    @Override
    public Chat createGroupChat(ChatGroupData data) throws CreateChatException, UserNotFoundException {
        if (!chatRepository.existsByChatName(data.getChatName())) {

            Chat chat = new Chat(data.getChatName(),
                    false,
                    data.getGroupUsersIds(),
                    data.getIdUser());
            chat.getIdUsers().add(data.getIdUser());
            chatRepository.save(chat);

            User userCreator = userRepository.getUserById(data.getIdUser());
            if (userCreator == null) {
                throw new UserNotFoundException(ErrorStatusCode.USER_DOES_NOT_EXIST);
            }
            userCreator.getChatsId().add(chat.getId());
            userRepository.save(userCreator);

            for (String userInGroup : data.getGroupUsersIds()) {
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
                            new WsMessageResponse(Command.CHAT_JOIN,
                                    new ChatGroupData(data.getIdUser(),
                                            chat.getId(),
                                            data.getChatName(),
                                            chat.getIdUsers()
                                    )
                            ))
                    );
            return chat;
        } else {
            throw new CreateChatException("Such a chat {} already exists");
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
                    .forEach(userId -> emitterService.sendMessageToUser(userId,
                            new WsMessageResponse(Command.CHAT_JOIN,
                                    new ChatData(userId,
                                            finalChat.getId(),
                                            finalChat.getChatName()
                                    )
                            ))
                    );

            return chat;
        } else {
            throw new CreateChatException("Such a chat {} already exists");
        }
    }

    @Override
    public Chat updateChat(ChatData data) throws UpdateChatException {

        Chat chat = chatRepository.findChatById(data.getIdChat());
        if (chat != null) {
            if (chat.getCreatorUserId().equals(data.getIdUser())) {
                chat.setChatName(data.getChatName());
                final Chat finalChat = chatRepository.save(chat);

                finalChat.getIdUsers().parallelStream()
                        .forEach(userId -> emitterService.sendMessageToUser(userId,
                                new WsMessageResponse(Command.CHAT_UPDATE,
                                        new ChatData(userId,
                                                finalChat.getId(),
                                                finalChat.getChatName()
                                        )
                                ))
                        );
                return chat;
            } else {
                throw new UpdateChatException("Only the author can update chat {}",
                        WsException.CHAT_ACCESS_ERROR.getNumberException()
                );
            }
        } else {
            throw new UpdateChatException("Chat {} doesn't exist",
                    WsException.CHAT_NOT_EXIT.getNumberException()
            );
        }
    }

    @Override
    public void deleteChat(ChatData data) throws DeleteChatException {

        Chat chat = chatRepository.findChatById(data.getIdChat());
        if (chat != null) {
            if (chat.getCreatorUserId().equals(data.getIdUser())) {
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
                        .forEach(userId -> emitterService.sendMessageToUser(userId,
                                new WsMessageResponse(Command.CHAT_DELETE,
                                        new ChatData(userId,
                                                chat.getId(),
                                                chat.getChatName()
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
                    WsException.CHAT_NOT_EXIT.getNumberException()
            );
        }
    }

    @Override
    public Chat leaveChat(ChatData data) {

        Chat chat = chatRepository.findChatById(data.getIdChat());
        if (chat != null) {
            chat.getIdUsers().remove(data.getIdUser());
            final Chat finalChat = chatRepository.save(chat);

            User user = userRepository.getUserById(data.getIdUser());
            user.getChatsId().remove(chat.getId());
            userRepository.save(user);

            finalChat.getIdUsers().parallelStream()
                    .forEach(userId -> emitterService.sendMessageToUser(userId,
                            new WsMessageResponse(Command.CHAT_LEAVE,
                                    new ChatData(userId,
                                            finalChat.getId(),
                                            finalChat.getChatName(),
                                            data.getIdUser()
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

        Chat chat = chatRepository.findChatById(data.getIdChat());

        if (chat != null) {
            List<String> listIdUsers = chat.getIdUsers();
            if (!chat.isPrivate() && !listIdUsers.contains(data.getIdUser())) {

                User user = userRepository.getUserById(data.getIdUser());
                user.getChatsId().add(data.getIdChat());
                userRepository.save(user);
                log.info(String.valueOf(listIdUsers));
                listIdUsers.add(data.getIdUser());
                final Chat finalChat = chatRepository.save(chat);
                log.info(String.valueOf(finalChat.getIdUsers()));
                finalChat.getIdUsers().parallelStream()
                        .forEach(userId -> emitterService.sendMessageToUser(userId,
                                new WsMessageResponse(Command.CHAT_JOIN,
                                        new ChatData(userId,
                                                finalChat.getId(),
                                                finalChat.getChatName(),
                                                data.getIdUser()
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
                    WsException.CHAT_NOT_EXIT.getNumberException()
            );
        }

        return chat;
    }

    @Override
    public List<Chat> showChatsByUser(ChatData data) throws UserNotFoundException {

        User user = userRepository.getUserById(data.getIdUser());
        if (user != null) {
            return chatRepository.getChatsByIdIn(user.getChatsId());
        } else {
            throw new UserNotFoundException(ErrorStatusCode.USER_DOES_NOT_EXIST);
        }
    }

}
