package org.zipli.socknet.ws;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@AllArgsConstructor
public class Event {
    private String command;
    private String message;
}

