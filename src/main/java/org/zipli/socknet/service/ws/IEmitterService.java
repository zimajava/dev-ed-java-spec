package org.zipli.socknet.service.ws;

import org.zipli.socknet.dto.WsMessageResponse;
import org.zipli.socknet.exception.session.CreateSocketException;
import org.zipli.socknet.exception.session.DeleteSessionException;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Map;

public interface IEmitterService {

    String addMessageEmitterByToken(String token, Sinks.Many<String> emitter) throws CreateSocketException;

    void deleteMessageEmitterByUserId(String userId, Sinks.Many<String> emitter) throws DeleteSessionException;

    Map<String, List<Sinks.Many<String>>> getMessageEmitter();

    void sendMessageToUser(String userId, WsMessageResponse wsMessage);
}
