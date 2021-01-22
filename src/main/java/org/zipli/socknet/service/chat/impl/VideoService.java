package org.zipli.socknet.service.chat.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.dto.Command;
import org.zipli.socknet.dto.response.WsMessageResponse;
import org.zipli.socknet.dto.video.VideoCallState;
import org.zipli.socknet.dto.video.VideoData;
import org.zipli.socknet.exception.ErrorStatusCode;
import org.zipli.socknet.exception.chat.ChatNotFoundException;
import org.zipli.socknet.exception.video.VideoCallException;
import org.zipli.socknet.repository.ChatRepository;
import org.zipli.socknet.repository.model.Chat;
import org.zipli.socknet.service.chat.IEmitterService;
import org.zipli.socknet.service.chat.IVideoService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class VideoService implements IVideoService {

    private final Map<String, VideoCallState> videoCallStorage = new ConcurrentHashMap<>();

    private final ChatRepository chatRepository;
    private final IEmitterService emitterService;

    public VideoService(ChatRepository chatRepository, IEmitterService emitterService) {
        this.chatRepository = chatRepository;
        this.emitterService = emitterService;
    }

    public VideoData startVideoCall(VideoData videoData) {
        VideoCallState videoCallState = new VideoCallState(videoData.getUserId(), new CopyOnWriteArrayList<>(), new CopyOnWriteArrayList<>());
        videoCallState.getIdUsersInCall().add(videoData.getUserId());
        videoCallStorage.put(videoData.getChatId(), videoCallState);

        Chat chat = chatRepository.findChatById(videoData.getChatId());
        if (chat == null) {
            throw new ChatNotFoundException(ErrorStatusCode.CHAT_NOT_EXISTS);
        }
        chat.getIdUsers().parallelStream()
                .forEach(userId -> emitterService.sendMessageToUser(userId,
                        new WsMessageResponse(Command.VIDEO_CALL_START, videoData)));

        List<String> usersWhoIsNotOnline = videoCallStorage.get(videoData.getChatId()).getIdUsersWhoIsNotOnline();
        usersWhoIsNotOnline.addAll(chat.getIdUsers());
        usersWhoIsNotOnline.removeAll(emitterService.getMessageEmitter().keySet());

        log.info("Start VideoCall in Chat {} by user {}.", videoData.getChatName(), videoData.getUserName());
        log.debug(videoCallStorage.get(videoData.getChatId()).toString());

        return videoData;
    }

    public VideoData joinVideoCall(VideoData videoData) {
        VideoCallState videoCallState = videoCallStorage.get(videoData.getChatId());
        if (videoCallState == null) {
            throw new VideoCallException("VideoCall {} doesn't exist",
                    ErrorStatusCode.VIDEO_CALL_EXCEPTION);
        }
        videoCallState.getIdUsersInCall()
                .add(videoData.getUserId());
        videoCallState.getIdUsersWhoIsNotOnline().remove(videoData.getUserId());
        Chat chat = chatRepository.findChatById(videoData.getChatId());
        if (chat == null) {
            throw new ChatNotFoundException(ErrorStatusCode.CHAT_NOT_EXISTS);
        }

        chat.getIdUsers().parallelStream()
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
                    ErrorStatusCode.CHAT_NOT_EXISTS);
        }
        videoCallState.getIdUsersInCall()
                .remove(chatData.getUserId());
        log.debug(videoCallState.toString());
        log.info("User {} leaved from videoCall in chat {}", chatData.getUserId(), chatData.getChatId());

        if (videoCallStorage
                .get(chatData.getChatId())
                .getIdUsersInCall()
                .size() < 2) {
            videoCallStorage.remove(chatData.getChatId());
            log.info("VideoCall in chat {} is over", chatData.getChatId());
        }
        Chat chat = chatRepository.findChatById(chatData.getChatId());
        if (chat == null) {
            throw new ChatNotFoundException(ErrorStatusCode.CHAT_NOT_EXISTS);
        }

        chat.getIdUsers().parallelStream()
                .forEach(userId -> emitterService.sendMessageToUser(userId,
                        new WsMessageResponse(Command.VIDEO_CALL_EXIT, chatData)));
        return chatData;
    }

}
