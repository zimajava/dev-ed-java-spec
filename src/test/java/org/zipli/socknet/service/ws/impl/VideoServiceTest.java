package org.zipli.socknet.service.ws.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.dto.video.VideoData;
import org.zipli.socknet.exception.chat.ChatNotFoundException;
import org.zipli.socknet.exception.video.VideoCallException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.repository.ChatRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class VideoServiceTest {

    private final VideoData videoData = new VideoData("123", "456", "Drew", "ChatName", "Signal");
    private Chat chat = new Chat(videoData.getChatName(), false, videoData.getUserId());
    private ChatData userData = new ChatData(videoData.getUserId(), videoData.getChatId());

    @Autowired
    VideoService videoService;

    @MockBean
    ChatRepository chatRepository;

    @Test
    public void startVideoCall_Pass() {
        Mockito.when(chatRepository.findChatById(videoData.getChatId())).thenReturn(chat);

        VideoData actualVideoData = videoService.startVideoCall(videoData);

        assertEquals(videoData.getChatId(), actualVideoData.getChatId());
        assertEquals(videoData.getUserId(), actualVideoData.getUserId());
        assertEquals(videoData.getChatName(), actualVideoData.getChatName());
        assertEquals(videoData.getUserName(), actualVideoData.getUserName());
        assertEquals(videoData.getSignal(), actualVideoData.getSignal());
    }

    @Test
    public void startVideoCall_Fail() {
        Mockito.when(chatRepository.findChatById(videoData.getChatId())).thenReturn(null);

        assertThrows(ChatNotFoundException.class, () -> videoService.startVideoCall(videoData));
    }

    @Test
    public void joinVideoCall_Pass() {
        Mockito.when(chatRepository.findChatById(videoData.getChatId())).thenReturn(chat);
        videoService.startVideoCall(videoData);
        VideoData actualVideoData = videoService.joinVideoCall(videoData);

        assertEquals(videoData.getChatId(), actualVideoData.getChatId());
        assertEquals(videoData.getUserId(), actualVideoData.getUserId());
        assertEquals(videoData.getChatName(), actualVideoData.getChatName());
        assertEquals(videoData.getUserName(), actualVideoData.getUserName());
        assertEquals(videoData.getSignal(), actualVideoData.getSignal());
    }

    @Test
    public void joinVideoCall_Fail() {
        Mockito.when(chatRepository.findChatById(videoData.getChatId())).thenReturn(null);

        assertThrows(ChatNotFoundException.class, () -> videoService.joinVideoCall(videoData));
    }

    @Test
    public void exitFromVideoCall_Pass() {
        Mockito.when(chatRepository.findChatById(videoData.getChatId())).thenReturn(chat);
        videoService.startVideoCall(videoData);

        ChatData actualVideoData = videoService.exitFromVideoCall(userData);

        assertEquals(videoData.getChatId(), actualVideoData.getChatId());
        assertEquals(videoData.getUserId(), actualVideoData.getUserId());
    }

    @Test
    void exitFromVideoCall_Fail() {
        Mockito.when(chatRepository.findChatById(videoData.getChatId())).thenReturn(null);

        assertThrows(VideoCallException.class, () -> videoService.exitFromVideoCall(userData));
    }
}
