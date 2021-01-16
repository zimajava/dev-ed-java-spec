package org.zipli.socknet.service.ws.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.BaseData;
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
        VideoCallState videoCallState = new VideoCallState(videoData.getIdUser(), new CopyOnWriteArrayList<>(), new CopyOnWriteArrayList<>());
        videoCallState.getIdUsersInCall().add(videoData.getIdUser());
        videoCallStorage.put(videoData.getIdChat(), videoCallState);

        Chat chat = chatRepository.findChatById(videoData.getIdChat());
        if (chat == null) {
            throw new ChatNotFoundException("Chat {} doesn't exist",
                    WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException());
        }
        chat.getIdUsers().parallelStream()
                .forEach(userId -> emitterService.sendMessageToUser(userId,
                        new WsMessageResponse(Command.VIDEO_CALL_START, videoData)));

        List<String> lisOfUsers = videoCallStorage.get(videoData.getIdChat()).getIdUsersWhoIsNotOnline();
        lisOfUsers.addAll(chat.getIdUsers());
        lisOfUsers.removeAll(emitterService.getMessageEmitter().keySet());

        log.info("Start VideoCall in Chat {} by user {}.", videoData.getChatName(), videoData.getUserName());
        log.debug(videoCallStorage.get(videoData.getIdChat()).toString());

        return videoData;
    }

    public VideoData joinVideoCall(VideoData videoData) {
        VideoCallState videoCallState = videoCallStorage.get(videoData.getIdChat());
        if (videoCallState == null) {
            throw new VideoCallException("VideoCall {} doesn't exist",
                    WsException.VIDEO_CALL_EXCEPTION.getNumberException());
        }
        videoCallState.getIdUsersInCall()
                .add(videoData.getIdUser());
        videoCallState.getIdUsersWhoIsNotOnline().remove(videoData.getIdUser());
        Chat chat = chatRepository.findChatById(videoData.getIdChat());
        if (chat == null) {
            throw new ChatNotFoundException("Chat {} doesn't exist",
                    WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException());
        }

        chat.getIdUsers().parallelStream()
                .forEach(userId -> emitterService.sendMessageToUser(userId,
                        new WsMessageResponse(Command.VIDEO_CALL_JOIN, videoData)));

        log.info("User {} successfully joined videoCall in Chat {}.", videoData.getUserName(), videoData.getChatName());
        log.debug(videoCallState.toString());

        return videoData;
    }

    public BaseData exitFromVideoCall(BaseData baseData) {
        VideoCallState videoCallState = videoCallStorage.get(baseData.getIdChat());
        if (videoCallState == null) {
            throw new VideoCallException("VideoCall {} doesn't exist",
                    WsException.VIDEO_CALL_EXCEPTION.getNumberException());
        }
        videoCallState.getIdUsersInCall()
                .remove(baseData.getIdUser());
        log.debug(videoCallState.toString());
        log.info("User {} leaved from videoCall in chat {}", baseData.getIdUser(), baseData.getIdChat());

        if (videoCallStorage
                .get(baseData.getIdChat())
                .getIdUsersInCall()
                .size() < 2) {
            videoCallStorage.remove(baseData.getIdChat());
            log.info("VideoCall in chat {} is over", baseData.getIdChat());
        }
        Chat chat = chatRepository.findChatById(baseData.getIdChat());
        if (chat == null) {
            throw new ChatNotFoundException("Chat {} doesn't exist",
                    WsException.CHAT_NOT_FOUND_EXCEPTION.getNumberException());
        }

        chat.getIdUsers().parallelStream()
                .forEach(userId -> emitterService.sendMessageToUser(userId,
                        new WsMessageResponse(Command.VIDEO_CALL_EXIT, baseData)));
        return baseData;
    }

}
