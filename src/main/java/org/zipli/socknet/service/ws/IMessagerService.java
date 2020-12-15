package org.zipli.socknet.service.ws;

import org.zipli.socknet.dto.WsMessage;
import org.zipli.socknet.exception.CreateChatException;
import org.zipli.socknet.exception.RemoveChatException;
import org.zipli.socknet.exception.UpdateChatException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;

import java.util.List;

public interface IMessagerService {

    Chat createGroupChat(WsMessage wsMessage) throws CreateChatException;//win
    Chat createPrivateChat(WsMessage wsMessage) throws CreateChatException; //win
    Chat updateChat(WsMessage wsMessage) throws UpdateChatException;//win
    void removeChat(WsMessage wsMessage) throws RemoveChatException;//win
    List<Chat> showChatsByUser(WsMessage wsMessage);//win
    Chat leaveChat(WsMessage wsMessage);//win
    Chat joinChat(WsMessage wsMessage);//win
    List<Message> getMessages(WsMessage wsMessage);//test
    Message sendMessage(WsMessage wsMessage);//win

}
