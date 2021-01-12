package org.zipli.socknet.service.ws.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.zipli.socknet.exception.CreateSocketException;
import org.zipli.socknet.exception.DeleteSessionException;
import org.zipli.socknet.model.User;
import org.zipli.socknet.repository.UserRepository;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.security.services.UserDetailsImpl;
import reactor.core.publisher.Sinks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Slf4j
@SpringBootTest
class EmitterServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    EmitterService emitterService = new EmitterService(userRepository, new JwtUtils());

    @Test
    void deleteMessageEmitterByUserId_Pass() {
        User user = new User("sdasda","sdasd","sdasdas","fdsghh");

        String token = jwtUtils.generateJwtToken(new UserDetailsImpl(user),user.getEmail());
        Sinks.Many<String> emitter = Sinks.many().multicast().directAllOrNothing();
        String userId="";
        try {
            userId = emitterService.addMessageEmitterByToken(token, emitter);
        } catch (CreateSocketException e) {
            e.printStackTrace();
        }

        try {
            emitterService.deleteMessageEmitterByUserId(userId, emitter);
        } catch (DeleteSessionException e) {
            e.printStackTrace();
        }

       assertFalse(emitterService.getMessageEmitterByUserId().containsValue(userId));
    }

    @Test
    void deleteMessageEmitterByUserId_Fail() {
        String userId = "hgjfby";

        try {
            Sinks.Many<String> emitter = Sinks.many().multicast().directAllOrNothing();
            emitterService.deleteMessageEmitterByUserId(userId, emitter);
        } catch (DeleteSessionException e) {
            assertEquals(e.getMessage(), "Can't delete message emitter");
        }
    }
}