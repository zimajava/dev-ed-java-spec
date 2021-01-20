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
import org.zipli.socknet.exception.file.FileDeleteException;
import org.zipli.socknet.exception.file.SendFileException;
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
import org.zipli.socknet.service.ws.IFileService;
import org.zipli.socknet.util.JsonUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.stream.Collectors;

import static org.zipli.socknet.dto.Command.ERROR_CREATE_CONNECT;

@Slf4j
@Component
public class WebFluxWebSocketHandler implements WebSocketHandler {
    private final IMessageService messageService;
    private final IFileService fileService;
    private final IEmitterService emitterService;
    private final IChatService chatService;
    private final IVideoService videoService;

    private FullChatData fullChatData;
    private BaseData baseData;
    private MessageData messageData;
    private VideoData videoData;
    private FileData fileData;
    private ChatData chatData;

    public WebFluxWebSocketHandler(IMessageService messageService, IFileService fileService,
                                   IEmitterService emitterService, IChatService chatService, IVideoService videoService) {
        this.messageService = messageService;
        this.fileService = fileService;
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
                fullChatData = (FullChatData) wsMessage.getData();
                try {
                    chatService.createChat(fullChatData);
                    log.info(commandSuccess,
                            eventCommand,
                            fullChatData.getUserId());
                } catch (CreateChatException e) {
                    log.error(commandFail, eventCommand, fullChatData.getUserId() + e.getMessage(), fullChatData.getChatName());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.ALREADY_EXISTS.getNumberException()))
                    );
                } catch (UserNotFoundException e) {
                    log.error(commandFail, eventCommand, fullChatData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.USER_NOT_FOUND_EXCEPTION.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, fullChatData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case CHAT_UPDATE:
                fullChatData = (FullChatData) wsMessage.getData();
                try {
                    chatService.updateChat(fullChatData);
                    log.info(commandSuccess,
                            eventCommand,
                            fullChatData.getUserId());
                    log.info("Chat {} update, his name {}",
                            fullChatData.getChatId(),
                            fullChatData.getChatName());
                } catch (UpdateChatException e) {
                    log.error(commandFail, eventCommand, fullChatData.getUserId());
                    log.error(e.getMessage(), fullChatData.getChatName());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    e.getNumberException())
                            )
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, fullChatData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException())
                            )
                    );
                }
                break;

            case CHAT_DELETE:
                chatData = (ChatData) wsMessage.getData();
                try {
                    chatService.deleteChat(chatData);
                    log.info(commandSuccess,
                            eventCommand,
                            chatData.getUserId());
                    log.info("Chat {} delete.",
                            chatData.getChatId());
                } catch (DeleteChatException e) {
                    log.error(commandFail, eventCommand, chatData.getUserId());
                    log.error(e.getMessage(), chatData.getChatId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    e.getMessage())
                            )
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, chatData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case CHAT_LEAVE:
                chatData = (ChatData) wsMessage.getData();
                try {
                    chatService.leaveChat(chatData);
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            chatData)));
                    log.info(commandSuccess,
                            eventCommand,
                            chatData.getUserId() + "For chat: " + chatData.getChatId());
                } catch (LeaveChatException e) {
                    log.error(commandFail, eventCommand, chatData.getUserId());
                    log.error(e.getMessage(), chatData.getChatId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXISTS.getNumberException())
                            )
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, chatData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case CHAT_USER_ADD:
                chatData = (ChatData) wsMessage.getData();
                try {
                    chatService.joinChat(chatData);
                    log.info(commandSuccess,
                            eventCommand,
                            chatData.getUserId() + "For chat: " + chatData.getChatId());
                } catch (JoinChatException e) {
                    log.error(commandFail, eventCommand, chatData.getUserId() + e.getMessage(), chatData.getChatId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_ACCESS_ERROR.getNumberException())
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

            case CHATS_GET_BY_USER_ID:
                baseData = wsMessage.getData();
                try {
                    List<Chat> chatsByUserId = chatService.showChatsByUser(baseData);
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            chatsByUserId)));
                    log.info(commandSuccess,
                            eventCommand,
                            baseData.getUserId());
                } catch (UserNotFoundException e) {
                    log.error(commandFail, eventCommand, baseData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.USER_NOT_FOUND_EXCEPTION.getNumberException())
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
                                    WsException.CHAT_NOT_EXISTS.getNumberException()))
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
                                    WsException.CHAT_NOT_EXISTS.getNumberException()))
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
                                    WsException.CHAT_NOT_EXISTS.getNumberException()))
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
                chatData = (ChatData) wsMessage.getData();
                try {
                    List<Message> messagesByChatId = messageService.getMessages(chatData);
                    List<MessageData> list = messagesByChatId.stream().map(MessageData::new).collect(Collectors.toList());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand, list)));
                    log.info(commandSuccess,
                            eventCommand,
                            chatData.getUserId()
                                    + "In chat: "
                                    + chatData.getChatId()
                    );
                } catch (GetMessageException e) {
                    log.error(commandFail, eventCommand, chatData.getUserId() + e.getMessage(), chatData.getChatId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXISTS.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, chatData.getUserId() + e.getMessage());
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
                chatData = (ChatData) wsMessage.getData();
                try {
                    videoService.exitFromVideoCall(chatData);
                    log.info(commandSuccess,
                            eventCommand,
                            chatData.getUserId()
                    );
                } catch (VideoCallException e) {
                    log.error(commandFail, eventCommand, chatData.getUserId());
                    log.error(e.getMessage(), chatData.getChatId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.VIDEO_CALL_EXCEPTION.getNumberException())
                    ));
                } catch (ChatNotFoundException e) {
                    log.error(commandFail, eventCommand, chatData.getUserId() + e.getMessage(), chatData.getChatId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, chatData.getUserId() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case FILE_SEND:
                fileData = (FileData) wsMessage.getData();
                try {
                    fileService.sendFile(fileData);
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, "File is successfully sended")));
                    log.info(commandSuccess, eventCommand, fileData.getUserId(), "To chat: ", fileData.getChatId());
                } catch (SendFileException e) {
                    log.error("Failed to load file in a GridFs {} reason {}", fileData.getFileId(), e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.FILE_WAS_NOT_LOADING_CORRECT.getNumberException()))
                    );
                }
                break;

            case FILE_DELETE:
                fileData = (FileData) wsMessage.getData();
                try {
                    fileService.deleteFile(fileData);
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, "File is successfully deleted")));
                    log.info(commandSuccess, eventCommand, fileData.getUserId(), "in chat: ", fileData.getChatId(), "file: ", fileData.getFileId());
                } catch (FileDeleteException e) {
                    log.error("Failed to find the file to delete or the creator of the file is wrong {} reason {}", fileData.getUserId(), e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.FILE_ACCESS_ERROR.getNumberException()))
                    );
                    break;
                }
        }
    }
}
