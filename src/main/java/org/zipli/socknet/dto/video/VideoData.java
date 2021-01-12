package org.zipli.socknet.dto.video;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zipli.socknet.dto.BaseData;

@Getter
@Setter
@NoArgsConstructor
public class VideoData extends BaseData {
    private String userName;
    private String chatName;
    private String signal;

    public VideoData(String idUser, String idChat, String userName, String chatName, String signal) {
        super(idUser, idChat);
        this.userName = userName;
        this.chatName = chatName;
        this.signal = signal;
    }
}
