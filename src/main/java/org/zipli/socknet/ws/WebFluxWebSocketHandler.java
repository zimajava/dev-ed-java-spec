package org.zipli.socknet.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.zipli.socknet.dto.*;
import org.zipli.socknet.dto.video.VideoData;
import org.zipli.socknet.exception.CreateSocketException;
import org.zipli.socknet.exception.DeleteSessionException;
import org.zipli.socknet.exception.video.VideoCallException;
import org.zipli.socknet.exception.WsException;
import org.zipli.socknet.exception.auth.UserNotFoundException;
import org.zipli.socknet.exception.chat.*;
import org.zipli.socknet.exception.message.MessageDeleteException;
import org.zipli.socknet.exception.message.MessageSendException;
import org.zipli.socknet.exception.message.MessageUpdateException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;
import org.zipli.socknet.service.ws.message.IMessageService;
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
        final String userId;
        try {
            userId = messageService.addMessageEmitterByToken(token, emitter);
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
                })
                .doOnComplete(() -> {
                    try {
                        messageService.deleteMessageEmitterByUserId(userId, emitter);
                    } catch (DeleteSessionException e) {
                        log.error("Error delete session {}", e.getMessage());
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
                    Chat groupChat = messageService.createGroupChat((ChatGroupData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            new ChatData(groupChat.getId(), groupChat.getChatName()))));
                } catch (CreateChatException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.ALREADY_EXISTS.getNumberException()))
                    );
                } catch (Exception e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case CHAT_PRIVATE_CREATE:
                try {
                    Chat privateChat = messageService.createPrivateChat((ChatData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            new ChatData(privateChat.getId(), privateChat.getChatName()))));
                } catch (CreateChatException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.ALREADY_EXISTS.getNumberException()))
                    );
                } catch (Exception e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case CHAT_UPDATE:
                try {
                    Chat updatedChat = messageService.updateChat((ChatData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            new ChatData(updatedChat.getId(), updatedChat.getChatName()))));
                } catch (UpdateChatException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    e.getNumberException())
                            )
                    );
                } catch (Exception e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException())
                            )
                    );
                }
                break;

            case CHAT_DELETE:
                try {
                    messageService.deleteChat((ChatData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, "Chat is successfully deleted")));
                } catch (DeleteChatException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    e.getMessage())
                            )
                    );
                } catch (Exception e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case CHAT_LEAVE:
                try {
                    Chat leavedChat = messageService.leaveChat((ChatData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            new ChatData(leavedChat.getId(), leavedChat.getChatName()))));
                } catch (LeaveChatException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXIT.getNumberException())
                            )
                    );
                } catch (Exception e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case CHAT_JOIN:
                try {
                    Chat joinedChat = messageService.joinChat((ChatData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            new ChatData(joinedChat.getId(), joinedChat.getChatName()))));
                } catch (JoinChatException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_ACCESS_ERROR.getNumberException())
                            )
                    );
                } catch (Exception e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException())
                            )
                    );
                }
                break;

            case CHATS_GET_BY_USER_ID:
                try {
                    List<Chat> chatsByUserId = messageService.showChatsByUser((ChatData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            chatsByUserId)));
                } catch (UserNotFoundException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.USER_NOT_FOUND_EXCEPTION.getNumberException())
                            )
                    );
                } catch (Exception e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException())
                            )
                    );
                }
                break;

            case MESSAGE_SEND:
                try {
                    Message newMessage = messageService.sendMessage((MessageData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            new MessageData(Collections.singletonList(newMessage)))));
                } catch (MessageSendException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXIT.getNumberException()))
                    );
                } catch (Exception e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case MESSAGE_UPDATE:
                try {
                    Message updatedMessage = messageService.updateMessage((MessageData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            new MessageData(Collections.singletonList(updatedMessage)))));
                } catch (MessageUpdateException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    e.getNumberException()))
                    );
                } catch (Exception e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
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
                            new WsMessageResponse(eventCommand,
                                    WsException.MESSAGE_ACCESS_ERROR.getNumberException()))
                    );
                } catch (UpdateChatException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXIT.getNumberException()))
                    );
                } catch (Exception e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case MESSAGES_GET_BY_CHAT_ID:
                try {
                    List<Message> messagesByChatId = messageService.getMessages((MessageData) wsMessage.getData());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            new MessageData(messagesByChatId))));
                } catch (GetMessageException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXIT.getNumberException()))
                    );
                } catch (Exception e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case VIDEO_CALL_START:
                try {
                    messageService.startVideoCall((VideoData) wsMessage.getData());
                } catch (VideoCallException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.VIDEO_CALL_EXCEPTION.getNumberException()))
                    );
                } catch (ChatNotFoundException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case VIDEO_CALL_JOIN:
                try {
                    messageService.joinVideoCall((VideoData) wsMessage.getData());
                } catch (VideoCallException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.VIDEO_CALL_EXCEPTION.getNumberException())
                    ));
                } catch (ChatNotFoundException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case VIDEO_CALL_EXIT:
                try {
                    messageService.exitFromVideoCall(wsMessage.getData());
                } catch (VideoCallException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.VIDEO_CALL_EXCEPTION.getNumberException())
                    ));
                } catch (ChatNotFoundException e) {
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException()))
                    );
                }
                break;
        }
    }
}
