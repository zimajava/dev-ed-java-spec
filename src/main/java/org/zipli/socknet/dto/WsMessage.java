package org.zipli.socknet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WsMessage {
    private Command command;
    private DataBase data;

    public WsMessage(Command eventCommand, String dataBase) {
    }
}
