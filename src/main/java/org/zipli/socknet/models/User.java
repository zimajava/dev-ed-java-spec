package org.zipli.socknet.models;

import lombok.Setter;
import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Document
@NoArgsConstructor
public class User {

    @Id
    private String id;

    @Indexed(unique=true)
    private String email;
    private String password;

    @Indexed(unique=true)
    private String userName;
    private String nickName;
    private boolean isConfirm;

    public User(String email, String password, String userName, String nickName) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.nickName = nickName;
        this.isConfirm = false;
    }

}
