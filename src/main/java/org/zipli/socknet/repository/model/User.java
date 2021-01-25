package org.zipli.socknet.repository.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private List<String> chatsId;
    private String avatar = "";

    public User(String email, String password, String userName, String nickName) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.nickName = nickName;
        this.isConfirm = false;
        chatsId = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isConfirm == user.isConfirm && Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(userName, user.userName) && Objects.equals(nickName, user.nickName) && Objects.equals(chatsId, user.chatsId) && Objects.equals(avatar, user.avatar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, userName, nickName, isConfirm, chatsId, avatar);
    }
}
