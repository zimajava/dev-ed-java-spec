package org.zipli.socknet.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomsResponse {
    private String roomId;
    private String nameRoom;

    @Override
    public String toString() {
        return "{" +
                "roomId=" + roomId +
                ", nameRoom=" + nameRoom +
                '}';
    }
}
