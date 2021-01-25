package org.zipli.socknet.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoomMessage {

    private String authorUserName;
    private String textMessage;
    private long date;

}
