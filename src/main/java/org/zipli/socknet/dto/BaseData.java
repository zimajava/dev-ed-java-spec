package org.zipli.socknet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaseData {
    private String userId;

    public BaseData(String userId) {
        this.userId = userId;
    }
}
