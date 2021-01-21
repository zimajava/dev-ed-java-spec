package org.zipli.socknet.service.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zipli.socknet.dto.*;
import org.zipli.socknet.dto.response.WsMessageResponse;
import org.zipli.socknet.dto.video.VideoData;
import org.zipli.socknet.exception.WsException;
import org.zipli.socknet.exception.auth.UserNotFoundException;
import org.zipli.socknet.exception.chat.*;
import org.zipli.socknet.exception.file.FileDeleteException;
import org.zipli.socknet.exception.file.SendFileException;
import org.zipli.socknet.exception.message.MessageDeleteException;
import org.zipli.socknet.exception.message.MessageSendException;
import org.zipli.socknet.exception.message.MessageUpdateException;
import org.zipli.socknet.exception.video.VideoCallException;
import org.zipli.socknet.repository.model.Chat;
import org.zipli.socknet.util.JsonUtils;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EventHandler {
    private static final String commandFail = "Command {} Fail with message {} and param {} ";

    private final IMessageService messageService;
    private final IFileService fileService;
    private final IChatService chatService;
    private final IVideoService videoService;

    public EventHandler(IMessageService messageService, IFileService fileService, IChatService chatService, IVideoService videoService) {
        this.messageService = messageService;
        this.fileService = fileService;
        this.chatService = chatService;
        this.videoService = videoService;
    }

    public void process(Sinks.Many<String> emitter, WsMessage message) {
        Command eventCommand = message.getCommand();

        switch (eventCommand) {
            case CHAT_CREATE:
                FullChatData chatData = (FullChatData) message.getData();
                try {
                    chatService.createChat(chatData);
                } catch (CreateChatException e) {
                    log.error(commandFail, eventCommand, chatData, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.ALREADY_EXISTS.getNumberException()))
                    );
                } catch (UserNotFoundException e) {
                    log.error(commandFail, eventCommand, chatData, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.USER_NOT_FOUND_EXCEPTION.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, chatData, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case CHAT_UPDATE:
                FullChatData chatUpdate = (FullChatData) message.getData();
                try {
                    chatService.updateChat(chatUpdate);

                } catch (UpdateChatException e) {
                    log.error(commandFail, eventCommand, chatUpdate, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    e.getNumberException())
                            )
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, chatUpdate, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException())
                            )
                    );
                }
                break;

            case CHAT_DELETE:
                ChatData baseData = (ChatData) message.getData();
                try {
                    chatService.deleteChat(baseData);

                } catch (DeleteChatException e) {
                    log.error(commandFail, eventCommand, baseData, e.getMessage());

                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    e.getMessage())
                            )
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, baseData, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case CHAT_LEAVE:
                ChatData chatLeave = (ChatData) message.getData();
                try {
                    chatService.leaveChat(chatLeave);

                } catch (LeaveChatException e) {
                    log.error(commandFail, eventCommand, chatLeave, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXISTS.getNumberException())
                            )
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, chatLeave, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case CHAT_USER_ADD:
                ChatData chatJoin = (ChatData) message.getData();
                try {
                    chatService.joinChat(chatJoin);

                } catch (JoinChatException e) {
                    log.error(commandFail, eventCommand, chatJoin, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_ACCESS_ERROR.getNumberException())
                            )
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, chatJoin, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException())
                            )
                    );
                }
                break;

            case CHATS_GET_BY_USER_ID:
                BaseData userData = message.getData();
                try {
                    List<Chat> chatsByUserId = chatService.showChatsByUser(userData);
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand,
                            chatsByUserId)));

                } catch (UserNotFoundException e) {
                    log.error(commandFail, eventCommand, userData, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.USER_NOT_FOUND_EXCEPTION.getNumberException())
                            )
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, userData, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException())
                            )
                    );
                }
                break;

            case MESSAGE_SEND:
                MessageData messageData = (MessageData) message.getData();
                try {
                    messageService.sendMessage(messageData);

                } catch (MessageSendException e) {
                    log.error(commandFail, eventCommand, messageData, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXISTS.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, messageData, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case MESSAGE_UPDATE:
                MessageData messageUpdate = (MessageData) message.getData();
                try {
                    messageService.updateMessage(messageUpdate);

                } catch (ChatNotFoundException e) {
                    log.error(commandFail, eventCommand, messageUpdate, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXISTS.getNumberException()))
                    );
                } catch (MessageUpdateException e) {
                    log.error(commandFail, eventCommand, messageUpdate, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    e.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, messageUpdate, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case MESSAGE_DELETE:
                MessageData messageDelete = (MessageData) message.getData();
                try {
                    messageService.deleteMessage(messageDelete);

                } catch (MessageDeleteException e) {
                    log.error(commandFail, eventCommand, messageDelete, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.MESSAGE_ACCESS_ERROR.getNumberException()))
                    );
                } catch (ChatNotFoundException e) {
                    log.error(commandFail, eventCommand, messageDelete, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXISTS.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, messageDelete, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.UNEXPECTED_EXCEPTION.getNumberException()))
                    );
                }
                break;

            case MESSAGES_GET_BY_CHAT_ID:
                ChatData messagesByChat = (ChatData) message.getData();
                try {

                    List<MessageData> messages = messageService.getMessages(messagesByChat)
                            .stream()
                            .map(MessageData::new)
                            .collect(Collectors.toList());

                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(new WsMessageResponse(eventCommand, messages)));

                } catch (GetMessageException e) {
                    log.error(commandFail, eventCommand, messagesByChat, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.CHAT_NOT_EXISTS.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, messagesByChat, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case VIDEO_CALL_START:
                VideoData videoData = (VideoData) message.getData();
                try {
                    videoService.startVideoCall(videoData);

                } catch (VideoCallException e) {
                    log.error(commandFail, eventCommand, videoData, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.VIDEO_CALL_EXCEPTION.getNumberException()))
                    );
                } catch (ChatNotFoundException e) {
                    log.error(commandFail, eventCommand, videoData, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, videoData, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case VIDEO_CALL_JOIN:
                VideoData videoCallJoin = (VideoData) message.getData();
                try {
                    videoService.joinVideoCall(videoCallJoin);

                } catch (VideoCallException e) {
                    log.error(commandFail, eventCommand, videoCallJoin, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.VIDEO_CALL_EXCEPTION.getNumberException())
                    ));
                } catch (ChatNotFoundException e) {
                    log.error(commandFail, eventCommand, videoCallJoin, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, videoCallJoin, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case VIDEO_CALL_EXIT:
                ChatData videoCallExit = (ChatData) message.getData();
                try {
                    videoService.exitFromVideoCall(videoCallExit);
                } catch (VideoCallException e) {
                    log.error(commandFail, eventCommand, videoCallExit, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.VIDEO_CALL_EXCEPTION.getNumberException())
                    ));
                } catch (ChatNotFoundException e) {
                    log.error(commandFail, eventCommand, videoCallExit, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException()))
                    );
                } catch (Exception e) {
                    log.error(commandFail, eventCommand, videoCallExit, e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, e.getMessage()))
                    );
                }
                break;

            case FILE_SEND:
                FileData fileData = (FileData) message.getData();
                try {
                    fileService.sendFile(fileData);
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, "File is successfully sent")));

                } catch (SendFileException e) {
                    log.error("Failed to load file in a GridFs {} reason {}", fileData.getFileId(), e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.FILE_WAS_NOT_LOADING_CORRECT.getNumberException()))
                    );
                }
                break;

            case FILE_DELETE:
                FileData fileDelete = (FileData) message.getData();
                try {
                    fileService.deleteFile(fileDelete);
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand, "File is successfully deleted")));
                } catch (FileDeleteException e) {
                    log.error("Failed to find the file to delete or the creator of the file is wrong {} reason {}", fileDelete.getUserId(), e.getMessage());
                    emitter.tryEmitNext(JsonUtils.jsonWriteHandle(
                            new WsMessageResponse(eventCommand,
                                    WsException.FILE_ACCESS_ERROR.getNumberException()))
                    );
                    break;
                }
        }
    }

}
