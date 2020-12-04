package org.zipli.socknet.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
@NoArgsConstructor
public class User {
    @Id
    private long id;

    private String email;
    private String password;
    private String userName;
    private String nickName;
    private boolean isConfirm;

    public User(long id, String email, String password, String userName, String nickName) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.nickName = nickName;
        this.isConfirm = false;
    }

}
