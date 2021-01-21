package org.zipli.socknet.dto.video;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zipli.socknet.dto.BaseData;

@Getter
@Setter
@NoArgsConstructor
public class VideoData extends BaseData {
    private String chatId;
    private String userName;
    private String chatName;
    private String signal;

    public VideoData(String idUser, String chatId, String userName, String chatName, String signal) {
        super(idUser);
        this.chatId = chatId;
        this.userName = userName;
        this.chatName = chatName;
        this.signal = signal;
    }
}
