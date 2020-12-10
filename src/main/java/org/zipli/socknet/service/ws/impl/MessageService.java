package org.zipli.socknet.service.ws.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.WsMessage;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.MessageRepository;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.service.ws.IMessageService;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService implements IMessageService {

    final UserRepository userRepository;

    final ChatRepository chatRepository;

    final MessageRepository messageRepository;

    public MessageService(UserRepository userRepository, ChatRepository chatRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Message sendMessageGlobal(WsMessage wsMessage) {
        return null;
    }

    @Override
    public Message sendMessagePrivate(WsMessage wsMessage) {
        return null;
    }

    @Override
    public boolean createChat(WsMessage wsMessage) {

        if (!chatRepository.existsByChatName(wsMessage.getNameChat())) {

            List<String> idUsers = new ArrayList<>();
            idUsers.add(wsMessage.getUserId());

            Chat chat = new Chat(wsMessage.getNameChat(),
                    false,
                    null,
                    idUsers,
                    wsMessage.getUserId());

            chatRepository.save(chat);

            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean removeChat(WsMessage wsMessage) {

        if (chatRepository.existsByChatNameAndCreatorUserId(wsMessage.getNameChat(), wsMessage.getUserId())){

            chatRepository.delete(chatRepository
                    .findChatById(wsMessage.getRoomId()));

            return true;
        }else {
            return false;
        }
    }

    @Override
    public List<Message> joinGlobalChat(WsMessage wsMessage) {

        Chat chat = chatRepository.findChatById(wsMessage.getRoomId());

        List<String> listIdUsers = chat.getIdUsers();
        List<String> listIdMessages = chat.getIdMessages();
        List<Message> messages = new ArrayList<>();

        if(!listIdUsers.contains(wsMessage.getUserId())){
            listIdUsers.add(wsMessage.getUserId());
            chatRepository.save(chat);//не уверен что пашет
        }

        for (String idMessage: listIdMessages) {
            messages.add(messageRepository.getMessageById(idMessage));
        }

        return messages;
    }

    @Override
    public boolean updateChat(WsMessage wsMessage) {
        return false;
    }

    @Override
    public List<Chat> showAllPrivateChat(WsMessage wsMessage) {
        return chatRepository.getChatsByPrivate(true);
    }

    @Override
    public List<Chat> showAllGroupChat(WsMessage wsMessage) {
        return chatRepository.getChatsByPrivate(false);
    }
}
