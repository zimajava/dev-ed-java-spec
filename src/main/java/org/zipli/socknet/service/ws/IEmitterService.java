package org.zipli.socknet.service.ws;

import org.zipli.socknet.exception.CreateSocketException;
import org.zipli.socknet.exception.DeleteSessionException;
import reactor.core.publisher.Sinks;

public interface IEmitterService {

    String addMessageEmitterByToken(String token, Sinks.Many<String> emitter) throws CreateSocketException;

    void deleteMessageEmitterByUserId(String userId, Sinks.Many<String> emitter) throws DeleteSessionException;
}
