package org.zipli.socknet.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoByRoomRequest {

    private String username;

    private String idUser;

    private String signals;

    private boolean authUser;

}
