package org.zipli.socknet.ws;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component("ReactiveWebSocketHandler")
public class ReactiveWebSocketHandler implements WebSocketHandler {
    private final Event event;

    public ReactiveWebSocketHandler(Event event) {
        this.event = event;
    }

    private static final ObjectMapper json = new ObjectMapper();

    private Flux<String> eventFlux = Flux.generate(sink -> {
        Event event = new Event(getCommand().toString(), getMessage().toString());
        try {
            sink.next(json.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            sink.error(e);
        }
    });

//
//    private Flux<String> intervalFlux = Flux.interval(Duration.ofMillis(1000L))
//            .zipWith(eventFlux, (time, event) -> event);

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession.send(/*intervalFlux*/eventFlux
                .map(webSocketSession::textMessage))
                .and(webSocketSession.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .log());
    }
}
