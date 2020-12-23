package org.zipli.socknet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.model.Message;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataBase {

    private String idUser;
    private String idChat;

    public DataBase(String idUser) {
        this.idUser = idUser;
    }
}
