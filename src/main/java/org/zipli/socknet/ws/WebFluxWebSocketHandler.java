package org.zipli.socknet.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.zipli.socknet.dto.Command;
import org.zipli.socknet.dto.WsMessage;
import org.zipli.socknet.dto.WsMessageResponse;
import org.zipli.socknet.dto.Data;
import org.zipli.socknet.exception.CreateChatException;
import org.zipli.socknet.exception.CreateSocketException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.service.ws.IMessagerService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import static org.zipli.socknet.dto.Command.ERROR_CREATE_CONNECT;

@Slf4j
@Component
public class WebFluxWebSocketHandler implements WebSocketHandler {
    private final IMessagerService messageService;
    private static final ObjectMapper json = new ObjectMapper();

    public WebFluxWebSocketHandler(IMessagerService messageService) {
        this.messageService = messageService;
    }

    @Override
    @SneakyThrows
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        String token = webSocketSession.getHandshakeInfo().getUri().getQuery().replace("token=", "");
        Sinks.Many<String> emitter = Sinks.many().multicast().directAllOrNothing();
        try {
            messageService.addMessageEmitterByToken(token, emitter);
        } catch (CreateSocketException e) {
            String response = json.writeValueAsString(new WsMessageResponse(ERROR_CREATE_CONNECT, e.getMessage()));
            return webSocketSession.send(Mono.just(webSocketSession.textMessage(response)));
        }

        Mono<Void> input = webSocketSession.receive()
                .doOnNext(message -> {
                    try {
                        WsMessage wsMessage = json.readValue(message.getPayloadAsText(), WsMessage.class);
                        eventProcessor(emitter, wsMessage);
                    } catch (Exception e) {
                        log.error("Error get message {}", e.getMessage());
                    }
                }).then();

        Flux<String> source = emitter.asFlux();
        Mono<Void> output = webSocketSession.send(source.map(webSocketSession::textMessage));

        return Mono.zip(input, output).then();
    }

    private void eventProcessor(Sinks.Many<String> emitter, WsMessage wsMessage) throws JsonProcessingException {
        Command eventCommand = wsMessage.getCommand();
        switch (eventCommand) {
            case CHAT_GROUP_CREATE:
                try {
                    Chat groupChat = messageService.createGroupChat(wsMessage.getData());
                    emitter.tryEmitNext(json.writeValueAsString(
                            new WsMessage(eventCommand, new Data(groupChat.getId(), groupChat.getChatName())))
                    );
                } catch (CreateChatException e) {
                    emitter.tryEmitNext(json.writeValueAsString(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;
            case CHAT_JOIN:
                messageService.joinChat(wsMessage.getData());
                emitter.tryEmitNext(json.writeValueAsString(new WsMessage(eventCommand, new Data())));
                break;
        }
    }

}
