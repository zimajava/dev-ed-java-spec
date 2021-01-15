package org.zipli.socknet.service.room;

import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.exception.KeyChatNotFoundException;
import org.zipli.socknet.exception.auth.InvalidTokenException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.service.ws.impl.ChatService;

@Service
public class RoomService {

    final JwtUtils jwtUtils;

    final ChatService chatService;

    final UserRepository userRepository;

    public RoomService(ChatService chatService, JwtUtils jwtUtils, UserRepository userRepository) {
        this.chatService = chatService;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    public String getIdChatByKey(String key) throws KeyChatNotFoundException {
        String idChat = chatService.getKeyMapAndIdChatHashMap().get(key);
        if (idChat != null) {
            return idChat;
        } else {
            throw new KeyChatNotFoundException("Key not found");
        }
    }

    public Chat joinRoom(String keyRoom, String token) throws KeyChatNotFoundException, InvalidTokenException {
        if (jwtUtils.validateJwtToken(token)) {
            String userName = jwtUtils.getUserNameFromJwtToken(token);
            String idChat = getIdChatByKey(keyRoom);
            User user = userRepository.findUserByUserName(userName);

            return chatService.joinChat(new ChatData(user.getId(), idChat, null));
        } else {
            throw new InvalidTokenException("Token {} no valid");
        }
    }
}
