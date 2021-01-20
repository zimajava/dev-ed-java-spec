package org.zipli.socknet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WsMessageResponse {
    private Command command;
    private Object data;
}
