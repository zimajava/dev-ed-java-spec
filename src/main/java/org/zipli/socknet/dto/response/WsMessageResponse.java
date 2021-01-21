package org.zipli.socknet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.zipli.socknet.dto.Command;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WsMessageResponse {
    private Command command;
    private Object data;
}
