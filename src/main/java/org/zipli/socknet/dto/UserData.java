package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserData {
    private String userId = "";

    public UserData(String userId) {
        this.userId = userId;
    }
}
