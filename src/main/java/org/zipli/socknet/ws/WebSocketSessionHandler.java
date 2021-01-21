package org.zipli.socknet.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.zipli.socknet.dto.WsMessage;
import org.zipli.socknet.dto.response.WsMessageResponse;
import org.zipli.socknet.exception.session.CreateSocketException;
import org.zipli.socknet.exception.session.DeleteSessionException;
import org.zipli.socknet.service.chat.EventHandler;
import org.zipli.socknet.service.chat.IEmitterService;
import org.zipli.socknet.util.JsonUtils;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import static org.zipli.socknet.dto.Command.ERROR_CREATE_CONNECT;

@Slf4j
@Component
public class WebSocketSessionHandler implements WebSocketHandler {

    private final EventHandler eventHandler;
    private final IEmitterService emitterService;

    public WebSocketSessionHandler(IEmitterService emitterService, EventHandler eventHandler) {
        this.emitterService = emitterService;
        this.eventHandler = eventHandler;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        String token = webSocketSession.getHandshakeInfo().getUri().getQuery().replace("token=", "");
        Sinks.Many<String> publisher = Sinks.many().multicast().directAllOrNothing();
        final String userId;
        try {
            userId = emitterService.addMessageEmitterByToken(token, publisher);
        } catch (CreateSocketException e) {
            String response = JsonUtils.jsonWriteHandle(new WsMessageResponse(ERROR_CREATE_CONNECT, e.getMessage()));
            return webSocketSession.send(Mono.just(webSocketSession.textMessage(response)));
        }

        return Mono.zip(
                webSocketSession.receive()
                        .doOnNext(message -> {
                            try {
                                eventHandler.process(publisher, JsonUtils.json.readValue(message.getPayloadAsText(), WsMessage.class));
                            } catch (Throwable e) {
                                log.error("Error processing message {} error class {} error message {} ", message, e.getClass().getSimpleName(), e.getMessage());
                            }
                        })
                        .doOnComplete(() -> {
                            try {
                                emitterService.deleteMessageEmitterByUserId(userId, publisher);
                            } catch (DeleteSessionException e) {
                                log.error("Error delete session {} for userId {} error class {} error message {} ",
                                        webSocketSession, userId, e.getClass().getSimpleName(), e.getMessage());
                            }
                        }).then(),

                webSocketSession.send(
                        publisher.asFlux()
                                .map(webSocketSession::textMessage)

                )).then();
    }

}
