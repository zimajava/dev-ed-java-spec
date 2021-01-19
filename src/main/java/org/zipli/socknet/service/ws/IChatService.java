package org.zipli.socknet.service.ws;

import org.zipli.socknet.dto.BaseData;
import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.dto.UserData;
import org.zipli.socknet.exception.auth.UserNotFoundException;
import org.zipli.socknet.exception.chat.*;
import org.zipli.socknet.model.Chat;

import java.util.List;

public interface IChatService {

    Chat createChat(ChatData data) throws CreateChatException, UserNotFoundException;

    Chat updateChat(ChatData data) throws UpdateChatException;

    void deleteChat(BaseData data) throws DeleteChatException;

    List<Chat> showChatsByUser(UserData data);

    Chat leaveChat(BaseData data) throws LeaveChatException;

    Chat joinChat(BaseData data) throws JoinChatException;


}
