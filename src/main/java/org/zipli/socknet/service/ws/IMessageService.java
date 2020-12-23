package org.zipli.socknet.service.ws;

import org.zipli.socknet.dto.Data;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;
import reactor.core.publisher.Sinks;

import java.util.List;

public interface IMessageService {

    Chat createGroupChat(Data data) throws CreateChatException;

    Chat createPrivateChat(Data data) throws CreateChatException;

    Chat updateChat(Data data) throws UpdateChatException;

    void removeChat(Data data) throws RemoveChatException;

    List<Chat> showChatsByUser(Data data);

    Chat leaveChat(Data data);

    Chat joinChat(Data data);

    List<Message> getMessages(Data data);

    Message sendMessage(Data data);

    void addMessageEmitterByToken(String token, Sinks.Many<String> emitter) throws CreateSocketException;

    Message updateMessage(Data data) throws UpdateChatException;

    void deleteMessage(Data data) throws MessageDeleteException;
}
