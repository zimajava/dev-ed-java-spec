package org.zipli.socknet.service.ws.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.Command;
import org.zipli.socknet.dto.MessageData;
import org.zipli.socknet.dto.WsMessageResponse;
import org.zipli.socknet.exception.ErrorStatusCodeWs;
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
    public List<Message> getMessages(MessageData data) throws GetMessageException {

        Chat chat = chatRepository.findChatById(data.getIdChat());
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

        Message message = new Message(data.getIdUser(), data.getIdChat(), new Date(), data.getTextMessage());
        final Message finalMessage = messageRepository.save(message);
        Chat chat = chatRepository.findChatById(data.getIdChat());
        if (chat != null) {
            chat.getIdMessages().add(message.getId());

            chat.getIdUsers().parallelStream()
                    .forEach(userId -> emitterService.sendMessageToUser(userId,
                            new WsMessageResponse(Command.MESSAGE_SEND,
                                    new MessageData(userId,
                                            chat.getId(),
                                            finalMessage.getId(),
                                            finalMessage.getTextMessage()
                                    )
                            ))
                    );
            chatRepository.save(chat);

            return message;
        } else {
            throw new ChatNotFoundException("Chat {} doesn't exist",
                    ErrorStatusCodeWs.CHAT_NOT_FOUND_EXCEPTION.getNumberException());
        }
    }

    @Override
    public Message updateMessage(MessageData data) throws MessageUpdateException, ChatNotFoundException {

        Message message = messageRepository.getMessageByIdAndAuthorId(data.getMessageId(), data.getIdUser());

        if (message != null) {

            final Chat finalChat = chatRepository.findChatById(data.getIdChat());
            if (finalChat != null) {
                message.setTextMessage(data.getTextMessage());
                final Message finalMessage = messageRepository.save(message);
                finalChat.getIdUsers().parallelStream()
                        .forEach(userId -> emitterService.sendMessageToUser(userId,
                                new WsMessageResponse(Command.MESSAGE_UPDATE,
                                        new MessageData(userId,
                                                finalChat.getId(),
                                                finalMessage.getId(),
                                                finalMessage.getTextMessage()
                                        )
                                ))
                        );
            } else {
                throw new ChatNotFoundException("Chat {} doesn't exist",
                        ErrorStatusCodeWs.CHAT_NOT_FOUND_EXCEPTION.getNumberException()
                );
            }
            return message;
        } else {
            throw new MessageUpdateException("Only the author can update message {}",
                    ErrorStatusCodeWs.CHAT_ACCESS_ERROR
            );
        }
    }

    @Override
    public void deleteMessage(MessageData data) throws MessageDeleteException, UpdateChatException {

        Message message = messageRepository.getMessageByIdAndAuthorId(data.getMessageId(), data.getIdUser());

        if (message != null) {
            Chat chat = chatRepository.findChatById(data.getIdChat());
            if (chat != null) {
                chat.getIdMessages().remove(message.getId());
                final Chat finalChat = chatRepository.save(chat);

                finalChat.getIdUsers().parallelStream()
                        .forEach(userId -> emitterService.sendMessageToUser(userId,
                                new WsMessageResponse(Command.MESSAGE_DELETE,
                                        new MessageData(userId,
                                                finalChat.getId(),
                                                message.getId(),
                                                message.getTextMessage()
                                        )
                                ))
                        );
            } else {
                throw new ChatNotFoundException("Chat {} doesn't exist",
                        ErrorStatusCodeWs.CHAT_NOT_FOUND_EXCEPTION.getNumberException()
                );
            }
            messageRepository.delete(message);
        } else {
            throw new MessageDeleteException("Only the author can delete message{}");
        }
    }

}
