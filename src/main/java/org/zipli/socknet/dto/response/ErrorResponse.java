package org.zipli.socknet.dto.response;

import lombok.*;
import org.zipli.socknet.exception.ErrorStatusCode;

@Getter
@Setter
@EqualsAndHashCode
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
