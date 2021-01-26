package org.zipli.socknet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zipli.socknet.exception.ErrorStatusCode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int code;
    private String reason;

    public ErrorResponse(ErrorStatusCode errorStatusCode) {
        this.code = errorStatusCode.getValue();
        this.reason = errorStatusCode.getMessage();
    }
}
