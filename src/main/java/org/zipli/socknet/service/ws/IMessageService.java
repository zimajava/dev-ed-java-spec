package org.zipli.socknet.service.ws;

import org.zipli.socknet.dto.MessageData;
import org.zipli.socknet.exception.chat.*;
import org.zipli.socknet.exception.message.MessageDeleteException;
import org.zipli.socknet.exception.message.MessageSendException;
import org.zipli.socknet.model.Message;

import java.util.List;

public interface IMessageService {

    List<Message> getMessages(MessageData data);

    Message sendMessage(MessageData data) throws MessageSendException;

    Message updateMessage(MessageData data) throws UpdateChatException;

    void deleteMessage(MessageData data) throws MessageDeleteException, UpdateChatException;

}
