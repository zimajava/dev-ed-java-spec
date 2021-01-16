package org.zipli.socknet.model;

import com.sun.mail.iap.ByteArray;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Document
@NoArgsConstructor
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;
    private String password;

    @Indexed(unique = true)
    private String userName;
    private String nickName;
    private boolean isConfirm;
    private boolean isGoogle;
    private List<String> chatsId;
    private byte[] avatar;

    public User(String email, String password, String userName, String nickName) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.nickName = nickName;
        this.isConfirm = false;
        this.isGoogle = false;
        chatsId = new ArrayList<>();
    }

    public User(String email, String nickName, boolean isConfirm, boolean isGoogle) {
        this.email = email;
        this.nickName = nickName;
        this.isConfirm = isConfirm;
        this.isGoogle = isGoogle;
    }
}
