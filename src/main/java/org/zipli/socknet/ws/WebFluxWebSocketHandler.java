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
import org.zipli.socknet.exception.file.FindFileException;
import org.zipli.socknet.exception.file.SaveFileException;
import org.zipli.socknet.exception.file.SendFileException;
import org.zipli.socknet.exception.message.MessageDeleteException;
import org.zipli.socknet.exception.message.MessageSendException;
import org.zipli.socknet.exception.message.MessageUpdateException;
import org.zipli.socknet.exception.video.VideoCallException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.File;
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

import java.util.Collections;
import java.util.List;

import static org.zipli.socknet.dto.Command.ERROR_CREATE_CONNECT;

@Slf4j
@Component
public class WebFluxWebSocketHandler implements WebSocketHandler {
    private final IMessageService messageService;
    private final IFileService fileService;
    private final IEmitterService emitterService;
    private final IChatService chatService;
    private final IVideoService videoService;

    private ChatGroupData groupData;
    private ChatData chatData;
    private MessageData messageData;
    private VideoData videoData;
    private FileData fileData;

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
            case CHAT_GROUP_CREATE:
                groupData = (ChatGroupData) wsMessage.getData();
                try {
                    Chat groupChat = chatService.createGroupChat(groupData);
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            new ChatData(groupChat.getId(), groupChat.getChatName()))));
                    log.info(commandSuccess,
                            eventCommand,
                            groupChat.getIdUsers());
                } catch (CreateChatException e) {
                    log.error(commandFail, eventCommand, groupData.getIdUser() + e.getMessage(), groupData.getChatName());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.ALREADY_EXISTS.getNumberException()))
                    );
                } catch (UserNotFoundException e) {
                    log.error(commandFail, eventCommand, groupData.getIdUser() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.USER_NOT_FOUND_EXCEPTION.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, groupData.getIdUser() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case CHAT_PRIVATE_CREATE:
                chatData = (ChatData) wsMessage.getData();
                try {
                    Chat privateChat = chatService.createPrivateChat(chatData);
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            new ChatData(privateChat.getId(), privateChat.getChatName()))));
                    log.info(commandSuccess,
                            eventCommand,
                            chatData.getIdUser());
                } catch (CreateChatException e) {
                    log.error(commandFail, eventCommand, messageData.getIdUser() + e.getMessage(), chatData.getChatName());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.ALREADY_EXISTS.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, chatData.getIdUser() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case CHAT_UPDATE:
                chatData = (ChatData) wsMessage.getData();
                try {
                    Chat updatedChat = chatService.updateChat(chatData);
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            new ChatData(updatedChat.getId(), updatedChat.getChatName()))));
                    log.info(commandSuccess,
                            eventCommand,
                            chatData.getIdUser());
                    log.info("Chat {} update, his name {}",
                            chatData.getIdChat(),
                            updatedChat.getChatName());
                } catch (UpdateChatException e) {
                    log.error(commandFail, eventCommand, messageData.getIdUser());
                    log.error(e.getMessage(), chatData.getChatName());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    e.getNumberException())
                            )
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, chatData.getIdUser() + e.getMessage());
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
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, "Chat is successfully deleted")));
                    log.info(commandSuccess,
                            eventCommand,
                            chatData.getIdUser());
                    log.info("Chat {} delete.",
                            chatData.getIdChat());
                } catch (DeleteChatException e) {
                    log.error(commandFail, eventCommand, messageData.getIdUser());
                    log.error(e.getMessage(), chatData.getChatName());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    e.getMessage())
                            )
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, chatData.getIdUser() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case CHAT_LEAVE:
                chatData = (ChatData) wsMessage.getData();
                try {
                    Chat leavedChat = chatService.leaveChat(chatData);
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            new ChatData(leavedChat.getId(), leavedChat.getChatName()))));
                    log.info(commandSuccess,
                            eventCommand,
                            chatData.getIdUser() + "For chat: " + chatData.getIdChat());
                } catch (LeaveChatException e) {
                    log.error(commandFail, eventCommand, messageData.getIdUser());
                    log.error(e.getMessage(), chatData.getChatName());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXISTS.getNumberException())
                            )
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, chatData.getIdUser() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case CHAT_JOIN:
                chatData = (ChatData) wsMessage.getData();
                try {
                    Chat joinedChat = chatService.joinChat(chatData);
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            new ChatData(joinedChat.getId(), joinedChat.getChatName()))));
                    log.info(commandSuccess,
                            eventCommand,
                            chatData.getIdUser() + "For chat: " + chatData.getIdChat());
                } catch (JoinChatException e) {
                    log.error(commandFail, eventCommand, messageData.getIdUser() + e.getMessage(), chatData.getChatName());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_ACCESS_ERROR.getNumberException())
                            )
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, chatData.getIdUser() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException())
                            )
                    );
                }
                break;

            case CHATS_GET_BY_USER_ID:
                chatData = (ChatData) wsMessage.getData();
                try {
                    List<Chat> chatsByUserId = chatService.showChatsByUser(chatData);
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            chatsByUserId)));
                    log.info(commandSuccess,
                            eventCommand,
                            chatData.getIdUser());
                } catch (UserNotFoundException e) {
                    log.error(commandFail, eventCommand, messageData.getIdUser() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.USER_NOT_FOUND_EXCEPTION.getNumberException())
                            )
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, chatData.getIdUser() + e.getMessage());
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
                    Message newMessage = messageService.sendMessage(messageData);
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            new MessageData(Collections.singletonList(newMessage)))));
                    log.info(commandSuccess,
                            eventCommand,
                            messageData.getIdUser()
                                    + "To chat: "
                                    + messageData.getIdChat()
                    );
                } catch (MessageSendException e) {
                    log.error(commandFail, eventCommand, messageData.getIdUser());
                    log.error(e.getMessage(), messageData.getIdChat());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXISTS.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, messageData.getIdUser() + e.getMessage());
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
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            new MessageData(Collections.singletonList(updatedMessage)))));
                    log.info(commandSuccess,
                            eventCommand,
                            messageData.getIdUser()
                                    + "To chat: "
                                    + messageData.getIdChat()
                                    + ". New message ("
                                    + updatedMessage +
                                    ")"
                    );
                } catch (ChatNotFoundException e) {
                    log.error(commandFail, eventCommand, messageData.getIdUser());
                    log.error(e.getMessage(), messageData.getIdChat());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXISTS.getNumberException()))
                    );
                } catch (MessageUpdateException e) {
                    log.error(commandFail, eventCommand, messageData.getIdUser());
                    log.error(e.getMessage(), messageData.getMessageId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    e.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, messageData.getIdUser() + e.getMessage());
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
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, "Message is successfully deleted")));
                    log.info(commandSuccess,
                            eventCommand,
                            messageData.getIdUser()
                                    + "In chat: "
                                    + messageData.getIdChat()
                                    + "Message:"
                                    + messageData.getMessageId()
                    );
                } catch (MessageDeleteException e) {
                    log.error(commandFail, eventCommand, messageData.getIdUser());
                    log.error(e.getMessage(), messageData.getMessageId());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.MESSAGE_ACCESS_ERROR.getNumberException()))
                    );
                } catch (ChatNotFoundException e) {
                    log.error(commandFail, eventCommand, messageData.getIdUser());
                    log.error(e.getMessage(), messageData.getIdChat());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXISTS.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, messageData.getIdUser() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case MESSAGES_GET_BY_CHAT_ID:
                messageData = (MessageData) wsMessage.getData();
                try {
                    List<Message> messagesByChatId = messageService.getMessages(messageData);
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            new MessageData(messagesByChatId))));
                    log.info(commandSuccess,
                            eventCommand,
                            messageData.getIdUser()
                                    + "In chat: "
                                    + messageData.getIdChat()
                    );
                } catch (GetMessageException e) {
                    log.error(commandFail, eventCommand, messageData.getIdUser() + e.getMessage(), messageData.getIdChat());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXISTS.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, messageData.getIdUser() + e.getMessage());
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
                            messageData.getIdUser()
                    );
                } catch (VideoCallException e) {
                    log.error(commandFail, eventCommand, videoData.getIdUser());
                    log.error(e.getMessage(), videoData.getIdChat());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.VIDEO_CALL_EXCEPTION.getNumberException()))
                    );
                } catch (ChatNotFoundException e) {
                    log.error(commandFail, eventCommand, videoData.getIdUser());
                    log.error(e.getMessage(), videoData.getIdChat());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, videoData.getIdUser() + e.getMessage());
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
                            messageData.getIdUser()
                    );
                } catch (VideoCallException e) {
                    log.error(e.getMessage(), videoData.getIdChat());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.VIDEO_CALL_EXCEPTION.getNumberException())
                    ));
                } catch (ChatNotFoundException e) {
                    log.error(commandFail, eventCommand, videoData.getIdUser());
                    log.error(e.getMessage(), videoData.getIdChat());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, videoData.getIdUser() + e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case VIDEO_CALL_EXIT:
                videoData = (VideoData) wsMessage.getData();
                try {
                    videoService.exitFromVideoCall(videoData);
                    log.info(commandSuccess,
                            eventCommand,
                            messageData.getIdUser()
                    );
                } catch (VideoCallException e) {
                    log.error(commandFail, eventCommand, messageData.getIdUser());
                    log.error(e.getMessage(), videoData.getIdChat());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.VIDEO_CALL_EXCEPTION.getNumberException())
                    ));
                } catch (ChatNotFoundException e) {
                    log.error(commandFail, eventCommand, messageData.getIdUser() + e.getMessage(), videoData.getIdChat());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, messageData.getIdUser() + e.getMessage());
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
                    log.info(commandSuccess, eventCommand, fileData.getIdUser(), "To chat: ", fileData.getIdChat());
//                } catch (UpdateChatException e) {
//                    log.error("Failed to get an appropriate chat {} reason {}", fileData.getIdChat(), e.getMessage());
//                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
//                            new WsMessageResponse(eventCommand,
//                                    WsException.CHAT_NOT_EXISTS.getNumberException()))
//                    );
//                } catch (SaveFileException e) {
//                    log.error("Failed to write file in a DB {} reason {}", fileData.getFileId(), e.getMessage());
//                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
//                            new WsMessageResponse(eventCommand,
//                                    WsException.GRIDFSFILE_IS_NOT_FOUND.getNumberException()))
//                    );
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
                    log.info(commandSuccess, eventCommand, fileData.getIdUser(), "in chat: ", fileData.getIdChat(), "file: ", fileData.getFileId());
                } catch (FileDeleteException e) {
                    log.error("Failed to find the file to delete or the creator of the file is wrong {} reason {}", fileData.getIdUser(), e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.FILE_ACCESS_ERROR.getNumberException()))
                    );
                } catch (FindFileException e) {
                    log.error("Failed to find a file {} reason {}", fileData.getFileId(), e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.FILE_IS_NOT_IN_A_DB.getNumberException()))
                    );
                    break;
                }
        }
    }
}
