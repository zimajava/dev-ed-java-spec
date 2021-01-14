package org.zipli.socknet.service.ws.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.zipli.socknet.dto.WsMessageResponse;
import org.zipli.socknet.exception.CreateSocketException;
import org.zipli.socknet.exception.DeleteSessionException;
import org.zipli.socknet.exception.SendMessageException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import reactor.core.publisher.Sinks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Slf4j
@SpringBootTest
class EmitterServiceTest {

    @MockBean
    UserRepository userRepository;

    @MockBean
    JwtUtils jwtUtils;

    @Autowired
    EmitterService emitterService = new EmitterService(userRepository, new JwtUtils());
    String token;
    Sinks.Many<String> emitter;
    String userId = "";
    User user = new User("sdasda", "sdasd", "sdasdas", "fdsghh");

    @BeforeEach
    void setUp() {
        user.setId("");
        token = "";
        emitter = Sinks.many().multicast().directAllOrNothing();
    }

    @Test
    void deleteMessageEmitterByUserId_Pass() {

        try {
            userId = emitterService.addMessageEmitterByToken(token, emitter);
            emitterService.deleteMessageEmitterByUserId(userId, emitter);
        } catch (DeleteSessionException | CreateSocketException e) {
            e.printStackTrace();
        }

        assertFalse(emitterService.getMessageEmitter().containsValue(userId));
    }

    @Test
    void deleteMessageEmitterByUserId_Fail() {
        try {
            Sinks.Many<String> emitter = Sinks.many().multicast().directAllOrNothing();
            emitterService.deleteMessageEmitterByUserId(userId, emitter);
        } catch (DeleteSessionException e) {
            assertEquals(e.getMessage(), "Can't delete message emitter");
        }
    }

    @Test
    void addMessageEmitterByToken_Pass() {

        Mockito.when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(user.getUserName());
        Mockito.when(userRepository.findUserByUserName(user.getUserName())).thenReturn(user);
        try {
            userId = emitterService.addMessageEmitterByToken(token, emitter);
        } catch (CreateSocketException e) {
            e.printStackTrace();
        }

        assertEquals(emitterService.getMessageEmitter().size(), 1);
    }

    @Test
    void addMessageEmitterByToken_Fail() {
        try {
            userId = emitterService.addMessageEmitterByToken(token, emitter);
        } catch (CreateSocketException e) {
            assertEquals(e.getMessage(), "Can't create connect to user, Exception cause: null on class NullPointerException");
        }
    }

    @Test
    void sendMessageToUser_Pass() {
        Mockito.when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(user.getUserName());
        Mockito.when(userRepository.findUserByUserName(user.getUserName())).thenReturn(user);
        try {
            userId = emitterService.addMessageEmitterByToken(token, emitter);
        } catch (CreateSocketException e) {
            e.printStackTrace();
        }
        emitterService.sendMessageToUser(userId,new WsMessageResponse());
    }

    @Test
    void sendMessageToUser_Fail() {
        emitterService.sendMessageToUser(userId, new WsMessageResponse());
    }
}