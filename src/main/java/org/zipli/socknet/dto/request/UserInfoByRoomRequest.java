package org.zipli.socknet.dto.request;

import lombok.*;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoByRoomRequest {

    private String userName;

    private String userId;

    private String signal;

}
