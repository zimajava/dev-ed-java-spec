package org.zipli.socknet.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
