package org.zipli.socknet.service.ws;

import org.zipli.socknet.dto.DataChat;
import org.zipli.socknet.dto.DataMessage;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;
import reactor.core.publisher.Sinks;

import java.util.List;

public interface IMessageService {

    Chat createGroupChat(DataChat data) throws CreateChatException;

    Chat createPrivateChat(DataChat data) throws CreateChatException;

    Chat updateChat(DataChat data) throws UpdateChatException;

    void removeChat(DataChat data) throws RemoveChatException;

    List<Chat> showChatsByUser(DataChat data);

    Chat leaveChat(DataChat data);

    Chat joinChat(DataChat data);

    List<Message> getMessages(DataMessage data);

    Message sendMessage(DataMessage data);

    void addMessageEmitterByToken(String token, Sinks.Many<String> emitter) throws CreateSocketException;

    Message updateMessage(DataMessage data) throws UpdateChatException;

    void deleteMessage(DataMessage data) throws MessageDeleteException, UpdateChatException;
}
