package org.zipli.socknet.service.ws.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.Command;
import org.zipli.socknet.dto.WsMessage;
import org.zipli.socknet.exception.CreateSocketException;
import org.zipli.socknet.exception.DeleteSessionException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.service.ws.IEmitterService;
import org.zipli.socknet.util.JsonUtils;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class EmitterService implements IEmitterService {

    private final UserRepository userRepository;

    private final JwtUtils jwtUtils;

    private final Map<String, List<Sinks.Many<String>>> messageEmitterByUserId = new ConcurrentHashMap<>();

    public EmitterService(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    public List<Sinks.Many<String>> getMessageEmitterByUserId(String userId) {
        return messageEmitterByUserId.get(userId);
    }

    @Override
    public String addMessageEmitterByToken(String token, Sinks.Many<String> emitter) throws CreateSocketException {
        try {
            String username = jwtUtils.getUserNameFromJwtToken(token);
            User user = userRepository.findUserByUserName(username);
            String userId = user.getId();
            messageEmitterByUserId.computeIfAbsent(userId, e -> new CopyOnWriteArrayList<>()).add(emitter);
            return userId;
        } catch (Exception e) {
            throw new CreateSocketException("Can't create connect to user, Exception cause: " + e.getMessage() + " on class " + e.getClass().getSimpleName());
        }
    }

    public void sendMessageToUser(String userId, WsMessage wsMessage) {
        List<Sinks.Many<String>> emittersByUser = messageEmitterByUserId.get(userId);
        if (emittersByUser != null) {
            emittersByUser.forEach(emitter -> emitter.tryEmitNext(JsonUtils.jsonWriteHandle(wsMessage)));
        } else {
            if (wsMessage.getCommand().equals(Command.CHAT_LEAVE) || wsMessage.getCommand().equals(Command.CHAT_JOIN)) {
                log.info("User = {userId: {} isn't online: {}, user: {} not sent.}", userId, wsMessage.getCommand(), wsMessage.getData().getIdUser());
            } else {
                log.info("User = {userId: {} isn't online: {} not sent.}", userId, wsMessage.getCommand());
            }
        }
    }

    @Override
    public void deleteMessageEmitterByUserId(String userId, Sinks.Many<String> emitter) throws DeleteSessionException {
        try {
            messageEmitterByUserId.getOrDefault(userId, new CopyOnWriteArrayList<>()).remove(emitter);
        } catch (Exception e) {
            throw new DeleteSessionException("Can't delete message emitter");
        }
    }

}
