package org.zipli.socknet.service.ws;

import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.dto.MessageData;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.exception.chat.*;
import org.zipli.socknet.exception.message.MessageDeleteException;
import org.zipli.socknet.exception.message.MessageSendException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;
import reactor.core.publisher.Sinks;

import java.util.List;

public interface IMessageService {

    Chat createGroupChat(ChatData data) throws CreateChatException;

    Chat createPrivateChat(ChatData data) throws CreateChatException;

    Chat updateChat(ChatData data) throws UpdateChatException;

    void deleteChat(ChatData data) throws DeleteChatException;

    List<Chat> showChatsByUser(ChatData data);

    Chat leaveChat(ChatData data) throws LeaveChatException;

    Chat joinChat(ChatData data) throws JoinChatException;

    List<Message> getMessages(MessageData data);

    Message sendMessage(MessageData data) throws MessageSendException;

    String addMessageEmitterByToken(String token, Sinks.Many<String> emitter) throws CreateSocketException;

    Message updateMessage(MessageData data) throws UpdateChatException;

    void deleteMessage(MessageData data) throws MessageDeleteException, UpdateChatException;

    void deleteMessageEmitterByUserId(String userId, Sinks.Many<String> emitter) throws DeleteSessionException;
}
