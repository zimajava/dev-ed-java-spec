package org.zipli.socknet.dto;

import lombok.Getter;

@Getter
public class WsMessage {

    private String textMessage;
    private String userId;
    private String roomId;
    private String date;
    private String nameChat;

}
