package org.zipli.socknet.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private int code;

    public ErrorResponse(int code) {
        this.code = code;
    }
}
