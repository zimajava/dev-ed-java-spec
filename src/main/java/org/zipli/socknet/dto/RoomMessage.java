package org.zipli.socknet.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoomMessage {

    private String authorUserName;
    private String roomId;
    private String textMessage;
    private long date;

}
