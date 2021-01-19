//package org.zipli.socknet.service.ws.impl;
//
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.zipli.socknet.dto.UserData;
//import org.zipli.socknet.dto.video.VideoData;
//import org.zipli.socknet.exception.chat.ChatNotFoundException;
//import org.zipli.socknet.exception.video.VideoCallException;
//import org.zipli.socknet.model.Chat;
//import org.zipli.socknet.repository.ChatRepository;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@SpringBootTest
//public class VideoServiceTest {
//
//    private final VideoData videoData = new VideoData("123", "456", "Drew", "ChatName", "Signal");
//    private Chat chat = new Chat(videoData.getChatName(), false, videoData.getIdUser());
//    private UserData userData = new UserData(videoData.getIdUser(), videoData.getIdChat());
//
//    @Autowired
//    VideoService videoService;
//
//    @MockBean
//    ChatRepository chatRepository;
//
//    @Test
//    public void startVideoCall_Pass() {
//        Mockito.when(chatRepository.findChatById(videoData.getIdChat())).thenReturn(chat);
//
//        VideoData actualVideoData = videoService.startVideoCall(videoData);
//
//        assertEquals(videoData.getIdChat(), actualVideoData.getIdChat());
//        assertEquals(videoData.getIdUser(), actualVideoData.getIdUser());
//        assertEquals(videoData.getChatName(), actualVideoData.getChatName());
//        assertEquals(videoData.getUserName(), actualVideoData.getUserName());
//        assertEquals(videoData.getSignal(), actualVideoData.getSignal());
//    }
//
//    @Test
//    public void startVideoCall_Fail() {
//        Mockito.when(chatRepository.findChatById(videoData.getIdChat())).thenReturn(null);
//
//        assertThrows(ChatNotFoundException.class, () -> videoService.startVideoCall(videoData));
//    }
//
//    @Test
//    public void joinVideoCall_Pass() {
//        Mockito.when(chatRepository.findChatById(videoData.getIdChat())).thenReturn(chat);
//        videoService.startVideoCall(videoData);
//        VideoData actualVideoData = videoService.joinVideoCall(videoData);
//
//        assertEquals(videoData.getIdChat(), actualVideoData.getIdChat());
//        assertEquals(videoData.getIdUser(), actualVideoData.getIdUser());
//        assertEquals(videoData.getChatName(), actualVideoData.getChatName());
//        assertEquals(videoData.getUserName(), actualVideoData.getUserName());
//        assertEquals(videoData.getSignal(), actualVideoData.getSignal());
//    }
//
//    @Test
//    public void joinVideoCall_Fail() {
//        Mockito.when(chatRepository.findChatById(videoData.getIdChat())).thenReturn(null);
//
//        assertThrows(ChatNotFoundException.class, () -> videoService.joinVideoCall(videoData));
//    }
//
//    @Test
//    public void exitFromVideoCall_Pass() {
//        Mockito.when(chatRepository.findChatById(videoData.getIdChat())).thenReturn(chat);
//        videoService.startVideoCall(videoData);
//
//        UserData actualVideoData = videoService.exitFromVideoCall(videoData);
//
//        assertEquals(videoData.getIdChat(), actualVideoData.getIdChat());
//        assertEquals(videoData.getIdUser(), actualVideoData.getIdUser());
//    }
//
//    @Test
//    void exitFromVideoCall_Fail() {
//        Mockito.when(chatRepository.findChatById(videoData.getIdChat())).thenReturn(null);
//
//        assertThrows(VideoCallException.class, () -> videoService.exitFromVideoCall(userData));
//    }
//}
