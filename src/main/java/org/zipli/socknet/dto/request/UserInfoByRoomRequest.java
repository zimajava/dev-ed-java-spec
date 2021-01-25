package org.zipli.socknet.dto.request;

import lombok.*;

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
