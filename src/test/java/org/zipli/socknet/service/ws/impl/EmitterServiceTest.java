package org.zipli.socknet.service.ws.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.zipli.socknet.dto.Command;
import org.zipli.socknet.dto.MessageData;
import org.zipli.socknet.dto.WsMessageResponse;
import org.zipli.socknet.exception.session.CreateSocketException;
import org.zipli.socknet.exception.session.DeleteSessionException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import reactor.core.publisher.Sinks;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    Logger logger = (Logger) LoggerFactory.getLogger(EmitterService.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();

    @BeforeEach
    void setUp() {
        user.setId("");
        token = "";
        emitter = Sinks.many().multicast().directAllOrNothing();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void addMessageEmitterByToken_Pass() throws CreateSocketException {
        Mockito.when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(user.getUserName());
        Mockito.when(userRepository.findUserByUserName(user.getUserName())).thenReturn(user);
        userId = emitterService.addMessageEmitterByToken(token, emitter);
        assertEquals(emitterService.getMessageEmitter().size(), 1);
    }

    @Test
    void sendMessageToUser_Pass() throws CreateSocketException {
        Mockito.when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(user.getUserName());
        Mockito.when(userRepository.findUserByUserName(user.getUserName())).thenReturn(user);

        userId = emitterService.addMessageEmitterByToken(token, emitter);

        Logger logger = (Logger) LoggerFactory.getLogger(EmitterService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        emitterService.sendMessageToUser(userId, new WsMessageResponse());

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(logsList.get(0).getMessage(),
                "User = {userId: {} isn online: {}, sent.}");
        assertEquals(Level.INFO, logsList.get(0)
                .getLevel());
    }

    @Test
    void sendMessageToUser_Fail() {
        Logger logger = (Logger) LoggerFactory.getLogger(EmitterService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        emitterService.sendMessageToUser("dsadas", new WsMessageResponse(Command.MESSAGE_SEND, new MessageData()));

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(logsList.get(0).getMessage(),
                "User = {userId: {} isn't online: {} not sent.}");
        assertEquals(Level.INFO, logsList.get(0)
                .getLevel());
    }

    @Test
    void getMessageEmitter() {
        assertNotNull(emitterService.getMessageEmitter());
    }

    @Test
    void deleteMessageEmitterByUserId_Pass() throws CreateSocketException, DeleteSessionException {
        Mockito.when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(user.getUserName());
        Mockito.when(userRepository.findUserByUserName(user.getUserName())).thenReturn(user);

        userId = emitterService.addMessageEmitterByToken(token, emitter);
        emitterService.deleteMessageEmitterByUserId(userId, emitter);
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
}