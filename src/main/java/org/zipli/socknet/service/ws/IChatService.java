package org.zipli.socknet.service.ws;

import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.dto.ChatGroupData;
import org.zipli.socknet.exception.auth.UserNotFoundException;
import org.zipli.socknet.exception.chat.*;
import org.zipli.socknet.model.Chat;

import java.util.List;

public interface IChatService {

    Chat createGroupChat(ChatGroupData data) throws CreateChatException, UserNotFoundException;

    Chat createPrivateChat(ChatData data) throws CreateChatException;

    Chat updateChat(ChatData data) throws UpdateChatException;

    void deleteChat(ChatData data) throws DeleteChatException;

    List<Chat> showChatsByUser(ChatData data);

    Chat leaveChat(ChatData data) throws LeaveChatException;

    Chat joinChat(ChatData data) throws JoinChatException;


}
