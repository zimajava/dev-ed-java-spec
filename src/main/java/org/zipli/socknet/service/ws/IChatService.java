package org.zipli.socknet.service.ws;

import org.zipli.socknet.dto.BaseData;
import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.dto.FullChatData;
import org.zipli.socknet.exception.auth.UserNotFoundException;
import org.zipli.socknet.exception.chat.*;
import org.zipli.socknet.model.Chat;

import java.util.List;

public interface IChatService {

    Chat createChat(FullChatData data) throws CreateChatException, UserNotFoundException;

    Chat updateChat(FullChatData data) throws UpdateChatException;

    void deleteChat(ChatData data) throws DeleteChatException;

    List<Chat> showChatsByUser(BaseData data);

    Chat leaveChat(ChatData data) throws LeaveChatException;

    Chat joinChat(ChatData data) throws JoinChatException;

}
