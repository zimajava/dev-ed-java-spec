package org.zipli.socknet.service.chat.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.dto.Command;
import org.zipli.socknet.dto.MessageData;
import org.zipli.socknet.dto.response.WsMessageResponse;
import org.zipli.socknet.exception.ErrorStatusCode;
import org.zipli.socknet.exception.chat.ChatNotFoundException;
import org.zipli.socknet.exception.chat.GetMessageException;
import org.zipli.socknet.exception.chat.UpdateChatException;
import org.zipli.socknet.exception.message.MessageDeleteException;
import org.zipli.socknet.exception.message.MessageSendException;
import org.zipli.socknet.exception.message.MessageUpdateException;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.MessageRepository;
import org.zipli.socknet.repository.model.Chat;
import org.zipli.socknet.repository.model.Message;
import org.zipli.socknet.service.chat.IEmitterService;
import org.zipli.socknet.service.chat.IMessageService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MessageService implements IMessageService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final IEmitterService emitterService;

    public MessageService(ChatRepository chatRepository, MessageRepository messageRepository, IEmitterService emitterService) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.emitterService = emitterService;
    }

    @Override
    public List<Message> getMessages(ChatData data) throws GetMessageException {

        Chat chat = chatRepository.findChatById(data.getChatId());
        if (chat != null) {
            List<String> listIdMessages = chat.getMessagesId();
            List<Message> messages = new ArrayList<>();
            for (String idMessage : listIdMessages) {
                messages.add(messageRepository.getMessageById(idMessage));
            }
            log.info("Get messages {} In chat:{} ", data.getUserId(), data.getChatId());
            return messages;
        } else {
            throw new GetMessageException(ErrorStatusCode.CHAT_NOT_EXISTS);
        }
    }

    @Override
    public Message sendMessage(MessageData data) throws MessageSendException, ChatNotFoundException {

        Message message = new Message(data.getUserId(), data.getChatId(), data.getTimestamp(), data.getTextMessage());
        final Message finalMessage = messageRepository.save(message);
        Chat chat = chatRepository.findChatById(data.getChatId());
        if (chat != null) {
            chat.getMessagesId().add(message.getId());

            chat.getUsersId().parallelStream()
                    .forEach(userId -> emitterService.sendMessageToUser(userId,
                            new WsMessageResponse(Command.MESSAGE_SEND,
                                    new MessageData(data.getUserId(),
                                            chat.getId(),
                                            finalMessage.getId(),
                                            finalMessage.getTextMessage(),
                                            finalMessage.getDate()
                                    )
                            ))
                    );
            chatRepository.save(chat);

            log.info("User {} save to chat {} new message {}", data.getUserId(), data.getChatId(), message.getId());

            return message;
        } else {
            throw new ChatNotFoundException(ErrorStatusCode.CHAT_NOT_EXISTS);
        }
    }

    @Override
    public Message updateMessage(MessageData data) throws MessageUpdateException, ChatNotFoundException {

        final Chat finalChat = chatRepository.findChatById(data.getChatId());

        if (finalChat != null) {
            final Message finalMessage = messageRepository.updateMessage(data);

            if (finalMessage != null) {
                log.info("UpdateMessage with userId {}  to chat {} with author {} new message {} ", data.getUserId(), finalMessage.getChatId(), finalMessage.getAuthorId(), finalMessage.getId());

                finalChat.getUsersId().parallelStream()
                        .forEach(userId -> emitterService.sendMessageToUser(userId,
                                new WsMessageResponse(Command.MESSAGE_UPDATE,
                                        new MessageData(data.getUserId(),
                                                finalChat.getId(),
                                                finalMessage.getId(),
                                                finalMessage.getTextMessage(),
                                                finalMessage.getDate()
                                        )
                                ))
                        );
                return finalMessage;
            } else {
                throw new MessageUpdateException(ErrorStatusCode.CHAT_ACCESS_ERROR);
            }
        } else {
            throw new ChatNotFoundException(ErrorStatusCode.CHAT_NOT_EXISTS);
        }
    }

    @Override
    public void deleteMessage(MessageData data) throws MessageDeleteException, UpdateChatException {

        Message message = messageRepository.getMessageByIdAndAuthorId(data.getMessageId(), data.getUserId());

        if (message != null) {
            Chat chat = chatRepository.findChatById(data.getChatId());
            if (chat != null) {
                chat.getMessagesId().remove(message.getId());
                final Chat finalChat = chatRepository.save(chat);

                log.info("DeleteMessage user {} in chat {} message {} ", data.getUserId(), data.getChatId(), data.getMessageId());

                finalChat.getUsersId().parallelStream()
                        .forEach(userId -> emitterService.sendMessageToUser(userId,
                                new WsMessageResponse(Command.MESSAGE_DELETE,
                                        new MessageData(data.getUserId(),
                                                finalChat.getId(),
                                                message.getId(),
                                                message.getTextMessage(),
                                                message.getDate()
                                        )
                                ))
                        );
            } else {
                throw new ChatNotFoundException(ErrorStatusCode.CHAT_NOT_EXISTS);
            }
            messageRepository.delete(message);
        } else {
            throw new MessageDeleteException(ErrorStatusCode.MESSAGE_ACCESS_ERROR);
        }
    }

}
