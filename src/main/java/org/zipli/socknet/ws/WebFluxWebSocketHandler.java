package org.zipli.socknet.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.zipli.socknet.dto.*;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;
import org.zipli.socknet.service.ws.IMessageService;
import org.zipli.socknet.util.JsonUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Collections;
import java.util.List;

import static org.zipli.socknet.dto.Command.ERROR_CREATE_CONNECT;

@Slf4j
@Component
public class WebFluxWebSocketHandler implements WebSocketHandler {
    private final IMessageService messageService;

    public WebFluxWebSocketHandler(IMessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        String token = webSocketSession.getHandshakeInfo().getUri().getQuery().replace("token=", "");
        Sinks.Many<String> emitter = Sinks.many().multicast().directAllOrNothing();
        try {
            messageService.addMessageEmitterByToken(token, emitter);
        } catch (CreateSocketException e) {
            String response = JsonUtils.jsonWriteHandle(new WsMessageResponse(ERROR_CREATE_CONNECT, e.getMessage()));
            return webSocketSession.send(Mono.just(webSocketSession.textMessage(response)));
        }

        Mono<Void> input = webSocketSession.receive()
                .doOnNext(message -> {
                    try {
                        WsMessage wsMessage = JsonUtils.json.readValue(message.getPayloadAsText(), WsMessage.class);
                        eventProcessor(emitter, wsMessage);
                    } catch (Exception e) {
                        log.error("Error get message {}", e.getMessage());
                    }
                }).then();

        Flux<String> source = emitter.asFlux();
        Mono<Void> output = webSocketSession.send(source.map(webSocketSession::textMessage));

        return Mono.zip(input, output).then();
    }

    private void eventProcessor(Sinks.Many<String> emitter, WsMessage wsMessage) {
        Command eventCommand = wsMessage.getCommand();
        switch (eventCommand) {
            case CHAT_GROUP_CREATE:
                try {
                    Chat groupChat = messageService.createGroupChat((ChatData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessage(eventCommand,
                            new ChatData(groupChat.getId(), groupChat.getChatName()))));
                } catch (CreateChatException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case CHAT_PRIVATE_CREATE:
                try {
                    Chat privateChat = messageService.createPrivateChat((ChatData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessage(eventCommand,
                            new ChatData(privateChat.getId(), privateChat.getChatName()))));
                } catch (CreateChatException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case CHAT_UPDATE:
                try {
                    Chat updatedChat = messageService.updateChat((ChatData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessage(eventCommand,
                            new ChatData(updatedChat.getId(), updatedChat.getChatName()))));
                } catch (UpdateChatException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case CHAT_DELETE:
                try {
                    messageService.removeChat((ChatData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, "Chat is successfully deleted")));
                } catch (RemoveChatException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case CHAT_LEAVE:
                try {
                    Chat leavedChat = messageService.leaveChat((ChatData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessage(eventCommand,
                            new ChatData(leavedChat.getId(), leavedChat.getChatName()))));
                } catch (Exception e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case CHAT_JOIN:
                try {
                    Chat joinedChat = messageService.joinChat((ChatData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessage(eventCommand,
                            new ChatData(joinedChat.getId(), joinedChat.getChatName()))));
                } catch (Exception e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case CHATS_GET_BY_USER_ID:
                try {
                    List<Chat> chatsByUserId = messageService.showChatsByUser((ChatData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            chatsByUserId)));
                } catch (Exception e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case MESSAGE_SEND:
                try {
                    Message newMessage = messageService.sendMessage((MessageData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessage(eventCommand,
                            new MessageData(Collections.singletonList(newMessage)))));
                } catch (Exception e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case MESSAGE_UPDATE:
                try {
                    Message updatedMessage = messageService.updateMessage((MessageData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessage(eventCommand,
                            new MessageData(Collections.singletonList(updatedMessage)))));
                } catch (MessageUpdateException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case MESSAGE_DELETE:
                try {
                    messageService.deleteMessage((MessageData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, "Message is successfully deleted")));
                } catch (MessageDeleteException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case MESSAGES_GET_BY_CHAT_ID:
                try {
                    List<Message> messagesByChatId = messageService.getMessages((MessageData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessage(eventCommand,
                            new MessageData(messagesByChatId))));
                } catch (Exception e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;
        }
    }
}
