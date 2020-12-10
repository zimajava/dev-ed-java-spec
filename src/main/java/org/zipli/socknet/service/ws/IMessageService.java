package org.zipli.socknet.service.ws;

import org.zipli.socknet.dto.WsMessage;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;

import java.util.List;

public interface IMessageService {

    Message sendMessageGlobal(WsMessage wsMessage);
    Message sendMessagePrivate(WsMessage wsMessage);
    boolean createChat(WsMessage wsMessage);
    boolean removeChat(WsMessage wsMessage);
    List<Message> joinGlobalChat(WsMessage wsMessage);
    boolean updateChat(WsMessage wsMessage);
    List<Chat> showAllPrivateChat(WsMessage wsMessage);
    List<Chat> showAllGroupChat(WsMessage wsMessage);
}
