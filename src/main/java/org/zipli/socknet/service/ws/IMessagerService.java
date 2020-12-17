package org.zipli.socknet.service.ws;

import org.zipli.socknet.dto.WsMessage;
import org.zipli.socknet.exception.CreateChatException;
import org.zipli.socknet.exception.CreateSocketException;
import org.zipli.socknet.exception.RemoveChatException;
import org.zipli.socknet.exception.UpdateChatException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;
import reactor.core.publisher.Sinks;

import java.util.List;

public interface IMessagerService {

    Chat createGroupChat(WsMessage wsMessage) throws CreateChatException;

    Chat createPrivateChat(WsMessage wsMessage) throws CreateChatException;

    Chat updateChat(WsMessage wsMessage) throws UpdateChatException;

    void removeChat(WsMessage wsMessage) throws RemoveChatException;

    List<Chat> showChatsByUser(WsMessage wsMessage);

    Chat leaveChat(WsMessage wsMessage);

    Chat joinChat(WsMessage wsMessage);

    List<Message> getMessages(WsMessage wsMessage);

    Message sendMessage(WsMessage wsMessage);

    void addMessageEmitterByToken(String token, Sinks.Many<String> emitter) throws CreateSocketException;
}
