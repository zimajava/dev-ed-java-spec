package org.zipli.socknet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Event {
    private Command command;
    private Message message;
}

