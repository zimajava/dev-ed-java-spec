package org.zipli.socknet.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.zipli.socknet.dto.*;
import org.zipli.socknet.dto.video.VideoData;
import org.zipli.socknet.exception.CreateSocketException;
import org.zipli.socknet.exception.DeleteSessionException;
import org.zipli.socknet.exception.WsException;
import org.zipli.socknet.exception.auth.UserNotFoundException;
import org.zipli.socknet.exception.chat.*;
import org.zipli.socknet.exception.message.MessageDeleteException;
import org.zipli.socknet.exception.message.MessageSendException;
import org.zipli.socknet.exception.message.MessageUpdateException;
import org.zipli.socknet.exception.video.VideoCallException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;
import org.zipli.socknet.service.ws.IChatService;
import org.zipli.socknet.service.ws.IEmitterService;
import org.zipli.socknet.service.ws.IMessageService;
import org.zipli.socknet.service.ws.IVideoService;
import org.zipli.socknet.util.JsonUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.List;

import static org.zipli.socknet.dto.Command.ERROR_CREATE_CONNECT;

@Slf4j
@Component
public class WebFluxWebSocketHandler implements WebSocketHandler {
    private final IMessageService messageService;
    private final IEmitterService emitterService;
    private final IChatService chatService;
    private final IVideoService videoService;

    private ChatData chatData;
    private UserData userData;
    private MessageData messageData;
    private VideoData videoData;
    private BaseData baseData;

    public WebFluxWebSocketHandler(IMessageService messageService, IEmitterService emitterService, IChatService chatService, IVideoService videoService) {
        this.messageService = messageService;
        this.emitterService = emitterService;
        this.chatService = chatService;
        this.videoService = videoService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        String token = webSocketSession.getHandshakeInfo().getUri().getQuery().replace("token=", "");
        Sinks.Many<String> emitter = Sinks.many().multicast().directAllOrNothing();
        final String userId;
        try {
            userId = emitterService.addMessageEmitterByToken(token, emitter);
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
                        emitterService.deleteMessageEmitterByUserId(userId, emitter);
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
        String commandSuccess = "Command {} Success for user {}. ";
        String commandFail = "Command {} Fail for user {}: ";
        switch (eventCommand) {
            case CHAT_CREATE:
                chatData = (ChatData) wsMessage.getData();
                try {
                    chatService.createChat(chatData);
                    log.info(commandSuccess,
                            eventCommand,
                            chatData.getUserId());
                } catch (CreateChatException e) {
                    log.error(commandFail, eventCommand, chatData.getUserId() + e.getMessage(), chatData.getChatName());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.ALREADY_EXISTS.getNumberException()))
                    );
                } catch (UserNotFoundException e) {
                    log.error(commandFail, eventCommand, chatData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.USER_NOT_FOUND_EXCEPTION.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, chatData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case CHAT_UPDATE:
                chatData = (ChatData) wsMessage.getData();
                try {
                    chatService.updateChat(chatData);
                    log.info(commandSuccess,
                            eventCommand,
                            chatData.getUserId());
                    log.info("Chat {} update, his name {}",
                            chatData.getChatId(),
                            chatData.getChatName());
                } catch (UpdateChatException e) {
                    log.error(commandFail, eventCommand, chatData.getUserId());
                    log.error(e.getMessage(), chatData.getChatName());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    e.getNumberException())
                            )
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, chatData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException())
                            )
                    );
                }
                break;

            case CHAT_DELETE:
                baseData = (BaseData) wsMessage.getData();
                try {
                    chatService.deleteChat(baseData);
                    log.info(commandSuccess,
                            eventCommand,
                            baseData.getUserId());
                    log.info("Chat {} delete.",
                            baseData.getChatId());
                } catch (DeleteChatException e) {
                    log.error(commandFail, eventCommand, baseData.getUserId());
                    log.error(e.getMessage(), baseData.getChatId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    e.getMessage())
                            )
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, baseData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case CHAT_LEAVE:
                baseData = (BaseData) wsMessage.getData();
                try {
                    chatService.leaveChat(baseData);
                    log.info(commandSuccess,
                            eventCommand,
                            baseData.getUserId() + "For chat: " + baseData.getChatId());
                } catch (LeaveChatException e) {
                    log.error(commandFail, eventCommand, baseData.getUserId());
                    log.error(e.getMessage(), baseData.getChatId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXIT.getNumberException())
                            )
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, baseData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case CHAT_USER_ADD:
                baseData = (BaseData) wsMessage.getData();
                try {
                    chatService.joinChat(baseData);
                    log.info(commandSuccess,
                            eventCommand,
                            baseData.getUserId() + "For chat: " + baseData.getChatId());
                } catch (JoinChatException e) {
                    log.error(commandFail, eventCommand, baseData.getUserId() + e.getMessage(), baseData.getChatId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_ACCESS_ERROR.getNumberException())
                            )
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, baseData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException())
                            )
                    );
                }
                break;

            case CHATS_GET_BY_USER_ID:
                userData = wsMessage.getData();
                try {
                    List<Chat> chatsByUserId = chatService.showChatsByUser(userData);
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            chatsByUserId)));
                    log.info(commandSuccess,
                            eventCommand,
                            userData.getUserId());
                } catch (UserNotFoundException e) {
                    log.error(commandFail, eventCommand, userData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.USER_NOT_FOUND_EXCEPTION.getNumberException())
                            )
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, userData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException())
                            )
                    );
                }
                break;

            case MESSAGE_SEND:
                messageData = (MessageData) wsMessage.getData();
                try {
                    messageService.sendMessage(messageData);
                    log.info(commandSuccess,
                            eventCommand,
                            messageData.getUserId()
                                    + "To chat: "
                                    + messageData.getChatId()
                    );
                } catch (MessageSendException e) {
                    log.error(commandFail, eventCommand, messageData.getUserId());
                    log.error(e.getMessage(), messageData.getChatId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXIT.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, messageData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case MESSAGE_UPDATE:
                messageData = (MessageData) wsMessage.getData();
                try {
                    Message updatedMessage = messageService.updateMessage(messageData);
                    log.info(commandSuccess,
                            eventCommand,
                            messageData.getUserId()
                                    + "To chat: "
                                    + messageData.getChatId()
                                    + ". New message ("
                                    + updatedMessage +
                                    ")"
                    );
                } catch (ChatNotFoundException e) {
                    log.error(commandFail, eventCommand, messageData.getUserId());
                    log.error(e.getMessage(), messageData.getChatId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXIT.getNumberException()))
                    );
                } catch (MessageUpdateException e) {
                    log.error(commandFail, eventCommand, messageData.getUserId());
                    log.error(e.getMessage(), messageData.getMessageId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    e.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, messageData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case MESSAGE_DELETE:
                messageData = (MessageData) wsMessage.getData();
                try {
                    messageService.deleteMessage(messageData);
                    log.info(commandSuccess,
                            eventCommand,
                            messageData.getUserId()
                                    + "In chat: "
                                    + messageData.getChatId()
                                    + "Message:"
                                    + messageData.getMessageId()
                    );
                } catch (MessageDeleteException e) {
                    log.error(commandFail, eventCommand, messageData.getUserId());
                    log.error(e.getMessage(), messageData.getMessageId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.MESSAGE_ACCESS_ERROR.getNumberException()))
                    );
                } catch (ChatNotFoundException e) {
                    log.error(commandFail, eventCommand, messageData.getUserId());
                    log.error(e.getMessage(), messageData.getChatId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXIT.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, messageData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case MESSAGES_GET_BY_CHAT_ID:
                baseData = (BaseData) wsMessage.getData();
                try {
                    List<Message> messagesByChatId = messageService.getMessages(baseData);
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            new MessageData(messagesByChatId))));
                    log.info(commandSuccess,
                            eventCommand,
                            baseData.getUserId()
                                    + "In chat: "
                                    + baseData.getChatId()
                    );
                } catch (GetMessageException e) {
                    log.error(commandFail, eventCommand, baseData.getUserId() + e.getMessage(), baseData.getChatId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXIT.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, baseData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case VIDEO_CALL_START:
                videoData = (VideoData) wsMessage.getData();
                try {
                    videoService.startVideoCall(videoData);
                    log.info(commandSuccess,
                            eventCommand,
                            videoData.getUserId()
                    );
                } catch (VideoCallException e) {
                    log.error(commandFail, eventCommand, videoData.getUserId());
                    log.error(e.getMessage(), videoData.getChatId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.VIDEO_CALL_EXCEPTION.getNumberException()))
                    );
                } catch (ChatNotFoundException e) {
                    log.error(commandFail, eventCommand, videoData.getUserId());
                    log.error(e.getMessage(), videoData.getChatId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, videoData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case VIDEO_CALL_JOIN:
                videoData = (VideoData) wsMessage.getData();
                try {
                    videoService.joinVideoCall(videoData);
                    log.info(commandSuccess,
                            eventCommand,
                            messageData.getUserId()
                    );
                } catch (VideoCallException e) {
                    log.error(e.getMessage(), videoData.getChatId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.VIDEO_CALL_EXCEPTION.getNumberException())
                    ));
                } catch (ChatNotFoundException e) {
                    log.error(commandFail, eventCommand, videoData.getUserId());
                    log.error(e.getMessage(), videoData.getChatId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, videoData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case VIDEO_CALL_EXIT:
                baseData = (BaseData) wsMessage.getData();
                try {
                    videoService.exitFromVideoCall(baseData);
                    log.info(commandSuccess,
                            eventCommand,
                            baseData.getUserId()
                    );
                } catch (VideoCallException e) {
                    log.error(commandFail, eventCommand, baseData.getUserId());
                    log.error(e.getMessage(), baseData.getChatId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.VIDEO_CALL_EXCEPTION.getNumberException())
                    ));
                } catch (ChatNotFoundException e) {
                    log.error(commandFail, eventCommand, baseData.getUserId() + e.getMessage(), baseData.getChatId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, baseData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;
        }
    }
}
