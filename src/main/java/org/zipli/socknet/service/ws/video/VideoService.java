package org.zipli.socknet.service.ws.video;

import org.zipli.socknet.dto.BaseData;
import org.zipli.socknet.dto.video.VideoCallState;
import org.zipli.socknet.dto.video.VideoData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VideoService implements IVideoService{
private final Map<String, VideoCallState> videoCallStorage = new ConcurrentHashMap<>();


    @Override
    public VideoData startVideoCall(VideoData videoData) {
        videoCallStorage.put(videoData.getIdChat(),new VideoCallState());
        return null;
    }

    @Override
    public VideoData joinVideoCall(VideoData videoData) {
        return null;
    }

    @Override
    public BaseData exitFromVideoCall(BaseData baseData) {
        return null;
    }
}
