package org.zipli.socknet.service.ws;

import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.dto.FullChatData;
import org.zipli.socknet.exception.auth.UserNotFoundException;
import org.zipli.socknet.exception.chat.*;
import org.zipli.socknet.model.Chat;

import java.util.List;

public interface IChatService {

    Chat createChat(ChatData data) throws CreateChatException, UserNotFoundException;

    Chat updateChat(FullChatData data) throws UpdateChatException;

    void deleteChat(FullChatData data) throws DeleteChatException;

    List<Chat> showChatsByUser(ChatData data);

    Chat leaveChat(FullChatData data) throws LeaveChatException;

    Chat joinChat(FullChatData data) throws JoinChatException;


}
