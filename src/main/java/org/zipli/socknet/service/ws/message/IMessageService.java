package org.zipli.socknet.service.ws.message;

import org.zipli.socknet.dto.BaseData;
import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.dto.ChatGroupData;
import org.zipli.socknet.dto.MessageData;
import org.zipli.socknet.dto.video.VideoData;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;
import reactor.core.publisher.Sinks;

import java.util.List;

public interface IMessageService {

    Chat createGroupChat(ChatGroupData data) throws CreateChatException;

    Chat createPrivateChat(ChatData data) throws CreateChatException;

    Chat updateChat(ChatData data) throws UpdateChatException;

    void removeChat(ChatData data) throws RemoveChatException;

    List<Chat> showChatsByUser(ChatData data);

    Chat leaveChat(ChatData data);

    Chat joinChat(ChatData data);

    List<Message> getMessages(MessageData data);

    Message sendMessage(MessageData data);

    String addMessageEmitterByToken(String token, Sinks.Many<String> emitter) throws CreateSocketException;

    Message updateMessage(MessageData data) throws UpdateChatException;

    void deleteMessage(MessageData data) throws MessageDeleteException, UpdateChatException;

    void deleteMessageEmitterByUserId(String userId, Sinks.Many<String> emitter) throws DeleteSessionException;

    VideoData startVideoCall(VideoData videoData);

    VideoData joinVideoCall(VideoData videoData);

    BaseData exitFromVideoCall(BaseData baseData);
}
