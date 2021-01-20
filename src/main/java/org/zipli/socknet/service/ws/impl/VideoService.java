package org.zipli.socknet.service.ws.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.dto.Command;
import org.zipli.socknet.dto.WsMessageResponse;
import org.zipli.socknet.dto.video.VideoCallState;
import org.zipli.socknet.dto.video.VideoData;
import org.zipli.socknet.exception.WsException;
import org.zipli.socknet.exception.chat.ChatNotFoundException;
import org.zipli.socknet.exception.video.VideoCallException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.service.ws.IEmitterService;
import org.zipli.socknet.service.ws.IVideoService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@Slf4j
@Service
public class VideoService implements IVideoService {

    private final ChatRepository chatRepository;
    private final IEmitterService emitterService;

    private final Map<String, VideoCallState> videoCallStorage = new ConcurrentHashMap<>();

    public VideoService(ChatRepository chatRepository, IEmitterService emitterService) {
        this.chatRepository = chatRepository;
        this.emitterService = emitterService;
    }

    public VideoData startVideoCall(VideoData videoData) {
        VideoCallState videoCallState = new VideoCallState(videoData.getUserId(), new CopyOnWriteArrayList<>(), new CopyOnWriteArrayList<>());
        videoCallState.getUsersInCallId().add(videoData.getUserId());
        videoCallStorage.put(videoData.getChatId(), videoCallState);

        Chat chat = chatRepository.findChatById(videoData.getChatId());
        if (chat == null) {
            throw new ChatNotFoundException("Chat {} doesn't exist",
                    WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException());
        }
        chat.getUsersId().parallelStream()
                .forEach(userId -> emitterService.sendMessageToUser(userId,
                        new WsMessageResponse(Command.VIDEO_CALL_START, videoData)));

        List<String> lisOfUsers = videoCallStorage.get(videoData.getChatId()).getUsersWhoIsNotOnlineId();
        lisOfUsers.addAll(chat.getUsersId());
        lisOfUsers.removeAll(emitterService.getMessageEmitter().keySet());

        log.info("Start VideoCall in Chat {} by user {}.", videoData.getChatName(), videoData.getUserName());
        log.debug(videoCallStorage.get(videoData.getChatId()).toString());

        return videoData;
    }

    public VideoData joinVideoCall(VideoData videoData) {
        VideoCallState videoCallState = videoCallStorage.get(videoData.getChatId());
        if (videoCallState == null) {
            throw new VideoCallException("VideoCall {} doesn't exist",
                    WsException.VIDEO_CALL_EXCEPTION.getNumberException());
        }
        videoCallState.getUsersInCallId()
                .add(videoData.getUserId());
        videoCallState.getUsersWhoIsNotOnlineId().remove(videoData.getUserId());
        Chat chat = chatRepository.findChatById(videoData.getChatId());
        if (chat == null) {
            throw new ChatNotFoundException("Chat {} doesn't exist",
                    WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException());
        }

        chat.getUsersId().parallelStream()
                .forEach(userId -> emitterService.sendMessageToUser(userId,
                        new WsMessageResponse(Command.VIDEO_CALL_JOIN, videoData)));

        log.info("User {} successfully joined videoCall in Chat {}.", videoData.getUserName(), videoData.getChatName());
        log.debug(videoCallState.toString());

        return videoData;
    }

    public ChatData exitFromVideoCall(ChatData chatData) {
        VideoCallState videoCallState = videoCallStorage.get(chatData.getChatId());
        if (videoCallState == null) {
            throw new VideoCallException("VideoCall {} doesn't exist",
                    WsException.VIDEO_CALL_EXCEPTION.getNumberException());
        }
        videoCallState.getUsersInCallId()
                .remove(chatData.getUserId());
        log.debug(videoCallState.toString());
        log.info("User {} leaved from videoCall in chat {}", chatData.getUserId(), chatData.getChatId());

        if (videoCallStorage
                .get(chatData.getChatId())
                .getUsersInCallId()
                .size() < 2) {
            videoCallStorage.remove(chatData.getChatId());
            log.info("VideoCall in chat {} is over", chatData.getChatId());
        }
        Chat chat = chatRepository.findChatById(chatData.getChatId());
        if (chat == null) {
            throw new ChatNotFoundException("Chat {} doesn't exist",
                    WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException());
        }

        chat.getUsersId().parallelStream()
                .forEach(userId -> emitterService.sendMessageToUser(userId,
                        new WsMessageResponse(Command.VIDEO_CALL_EXIT, chatData)));
        return chatData;
    }

}
