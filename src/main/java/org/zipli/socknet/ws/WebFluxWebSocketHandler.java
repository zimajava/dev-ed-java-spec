package org.zipli.socknet.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.zipli.socknet.dto.*;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;
import org.zipli.socknet.service.ws.IMessageService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.zipli.socknet.dto.Command.ERROR_CREATE_CONNECT;

@Slf4j
@Component
public class WebFluxWebSocketHandler implements WebSocketHandler {
    private final IMessageService messageService;
    private static final ObjectMapper json = new ObjectMapper();

    public WebFluxWebSocketHandler(IMessageService messageService) {
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
                    Chat groupChat = messageService.createGroupChat((DataChat) wsMessage.getData());
                    DataChat dataBase = new DataChat(groupChat.getId(), groupChat.getChatName());
                    emitter.tryEmitNext(json.writeValueAsString(new WsMessage(eventCommand,
                            dataBase)));
                } catch (CreateChatException e) {
                    emitter.tryEmitNext(json.writeValueAsString(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case CHAT_PRIVATE_CREATE:
                try {
                    Chat privateChat = messageService.createPrivateChat((DataChat) wsMessage.getData());
                    DataChat dataBase = new DataChat(privateChat.getId(), privateChat.getChatName());
                    emitter.tryEmitNext(json.writeValueAsString(new WsMessage(eventCommand,
                            dataBase)));
                } catch (CreateChatException e) {
                    emitter.tryEmitNext(json.writeValueAsString(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case CHAT_UPDATE:
                try {
                    Chat updatedChat = messageService.updateChat((DataChat) wsMessage.getData());
                    DataChat dataBase = new DataChat(updatedChat.getId(), updatedChat.getChatName());
                    emitter.tryEmitNext(json.writeValueAsString(new WsMessage(eventCommand,
                            dataBase)));
                } catch (UpdateChatException e) {
                    emitter.tryEmitNext(json.writeValueAsString(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case CHAT_DELETE:
                try {
                    messageService.removeChat((DataChat) wsMessage.getData());
                    emitter.tryEmitNext(json.writeValueAsString(
                            new WsMessage(eventCommand,"Chat is successfully deleted")));
                } catch (RemoveChatException e) {
                    emitter.tryEmitNext(json.writeValueAsString(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case CHAT_LEAVE:
                try {
                    Chat leavedChat = messageService.leaveChat((DataChat) wsMessage.getData());
                    DataChat dataBase = new DataChat(leavedChat.getId(), leavedChat.getChatName());
                    emitter.tryEmitNext(json.writeValueAsString(new WsMessage(eventCommand,
                            dataBase)));
                } catch (Exception e) {
                    emitter.tryEmitNext(json.writeValueAsString(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case CHAT_JOIN:
                try {
                    Chat joinedChat = messageService.joinChat((DataChat) wsMessage.getData());
                    DataChat dataBase = new DataChat(joinedChat.getId(), joinedChat.getChatName());
                    emitter.tryEmitNext(json.writeValueAsString(new WsMessage(eventCommand,
                            dataBase)));
                } catch (Exception e) {
                    emitter.tryEmitNext(json.writeValueAsString(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case CHATS_GET_BY_USER_ID:
                try {
                    List<Chat> chatsByUserId = messageService.showChatsByUser((DataChat) wsMessage.getData());
                    emitter.tryEmitNext(json.writeValueAsString(new WsMessage(eventCommand,
                            new DataChat(chatsByUserId))));
                } catch (Exception e) {
                    emitter.tryEmitNext(json.writeValueAsString(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case MESSAGE_SEND:
                try {
                    Message newMessage = messageService.sendMessage((DataMessage) wsMessage.getData());
                    DataMessage dataBase = new DataMessage(Collections.singletonList(newMessage));
                    emitter.tryEmitNext(json.writeValueAsString(new WsMessage(eventCommand, dataBase)));
                } catch (Exception e) {
                    emitter.tryEmitNext(json.writeValueAsString(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case MESSAGE_UPDATE:
                try {
                    Message updatedMessage = messageService.updateMessage((DataMessage) wsMessage.getData());
                    DataChat dataBase = new DataChat(null, updatedMessage.getId());
                    emitter.tryEmitNext(json.writeValueAsString(new WsMessage(eventCommand,
                            dataBase)));
                } catch (MessageUpdateException e) {
                    emitter.tryEmitNext(json.writeValueAsString(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case MESSAGE_DELETE:
                try {
                    messageService.deleteMessage((DataMessage) wsMessage.getData());
                    emitter.tryEmitNext(json.writeValueAsString(
                            new WsMessage(eventCommand, "Message is successfully deleted")));
                } catch (MessageDeleteException e) {
                    emitter.tryEmitNext(json.writeValueAsString(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case MESSAGES_GET_BY_CHAT_ID:
                try {
                    List<Message> messagesByChatId = messageService.getMessages((DataMessage) wsMessage.getData());
                    emitter.tryEmitNext(json.writeValueAsString(new WsMessage(eventCommand,
                            new DataMessage(messagesByChatId))));
                } catch (Exception e) {
                    emitter.tryEmitNext(json.writeValueAsString(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;
        }
    }
}
