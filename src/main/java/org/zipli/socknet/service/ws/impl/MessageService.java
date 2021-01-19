package org.zipli.socknet.service.ws.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.BaseData;
import org.zipli.socknet.dto.Command;
import org.zipli.socknet.dto.MessageData;
import org.zipli.socknet.dto.WsMessageResponse;
import org.zipli.socknet.exception.WsException;
import org.zipli.socknet.exception.chat.ChatNotFoundException;
import org.zipli.socknet.exception.chat.GetMessageException;
import org.zipli.socknet.exception.chat.UpdateChatException;
import org.zipli.socknet.exception.message.MessageDeleteException;
import org.zipli.socknet.exception.message.MessageSendException;
import org.zipli.socknet.exception.message.MessageUpdateException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.MessageRepository;
import org.zipli.socknet.service.ws.IEmitterService;
import org.zipli.socknet.service.ws.IMessageService;

import java.util.ArrayList;
import java.util.Date;
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
    public List<Message> getMessages(BaseData data) throws GetMessageException {

        Chat chat = chatRepository.findChatById(data.getChatId());
        if (chat != null) {
            List<String> listIdMessages = chat.getIdMessages();
            List<Message> messages = new ArrayList<>();
            for (String idMessage : listIdMessages) {
                messages.add(messageRepository.getMessageById(idMessage));
            }
            return messages;
        } else {
            throw new GetMessageException("Chat{} doesn't exist");
        }
    }

    @Override
    public Message sendMessage(MessageData data) throws MessageSendException, ChatNotFoundException {

        Message message = new Message(data.getUserId(), data.getChatId(), data.getTimestamp(), data.getTextMessage());
        final Message finalMessage = messageRepository.save(message);
        Chat chat = chatRepository.findChatById(data.getChatId());
        if (chat != null) {
            chat.getIdMessages().add(message.getId());

            chat.getIdUsers().parallelStream()
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

            return message;
        } else {
            throw new ChatNotFoundException("Chat {} doesn't exist",
                    WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException());
        }
    }

    @Override
    public Message updateMessage(MessageData data) throws MessageUpdateException, ChatNotFoundException {

        Message message = messageRepository.getMessageByIdAndAuthorId(data.getMessageId(), data.getUserId());

        if (message != null) {

            final Chat finalChat = chatRepository.findChatById(data.getChatId());
            if (finalChat != null) {
                message.setTextMessage(data.getTextMessage());
                final Message finalMessage = messageRepository.save(message);
                finalChat.getIdUsers().parallelStream()
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
            } else {
                throw new ChatNotFoundException("Chat {} doesn't exist",
                        WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException()
                );
            }
            return message;
        } else {
            throw new MessageUpdateException("Only the author can update message {}",
                    WsException.CHAT_ACCESS_ERROR.getNumberException()
            );
        }
    }

    @Override
    public void deleteMessage(MessageData data) throws MessageDeleteException, UpdateChatException {

        Message message = messageRepository.getMessageByIdAndAuthorId(data.getMessageId(), data.getUserId());

        if (message != null) {
            Chat chat = chatRepository.findChatById(data.getChatId());
            if (chat != null) {
                chat.getIdMessages().remove(message.getId());
                final Chat finalChat = chatRepository.save(chat);

                finalChat.getIdUsers().parallelStream()
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
                throw new ChatNotFoundException("Chat {} doesn't exist",
                        WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException()
                );
            }
            messageRepository.delete(message);
        } else {
            throw new MessageDeleteException("Only the author can delete message{}");
        }
    }

}
