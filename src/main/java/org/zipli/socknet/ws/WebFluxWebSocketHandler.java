package org.zipli.socknet.ws;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.zipli.socknet.dto.Command;
import org.zipli.socknet.dto.Event;
import org.zipli.socknet.dto.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class WebFluxWebSocketHandler implements WebSocketHandler {
    private static final ObjectMapper json = new ObjectMapper();

    private Event event;

    private Flux<String> eventFlux(Command command, Message message) {

        Flux<String> flux = Flux.generate(sink -> {
            event = new Event(command, message);
            try {
                sink.next(json.writeValueAsString(event));
            } catch (JsonProcessingException e) {
                sink.error(e);
            }
        });
        return flux;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession.send(eventFlux(event.getCommand(), event.getMessage())
                .map(webSocketSession::textMessage))
                .and(webSocketSession.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .log());
    }

}
