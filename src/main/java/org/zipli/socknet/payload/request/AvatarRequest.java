package org.zipli.socknet.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class AvatarRequest {
    private String userId;
    private byte[] avatar;
}
