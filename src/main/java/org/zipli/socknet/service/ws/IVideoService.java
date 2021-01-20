package org.zipli.socknet.service.ws;

import org.zipli.socknet.dto.ChatData;
import org.zipli.socknet.dto.video.VideoData;

public interface IVideoService {

    VideoData startVideoCall(VideoData videoData);

    VideoData joinVideoCall(VideoData videoData);

    ChatData exitFromVideoCall(ChatData ChatData);

}
