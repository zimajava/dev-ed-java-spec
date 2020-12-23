package org.zipli.socknet.payload.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Language;
import org.springframework.lang.NonNull;

import javax.validation.constraints.*;

@Getter
@Setter
public class SignupRequest {
    @NotBlank(message = "Email can't be empty")
    @NotNull(message = "Email can't be null")
    @Email
    @Size(max = 50)
    @Pattern(regexp = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,}$")
    private String email;

    @NotBlank(message = "Password can't be empty")
    @NotNull(message = "Password can't be null")
    @Size(min = 8, max = 16)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,16}$")
    private String password;

    @NotBlank(message = "User name can't be empty")
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_-]{8,16}$")
    private String userName;

    @NotBlank(message = "Nickname can't be empty")
    @Pattern(regexp = "^[a-zA-Z0-9_ .-]{8,16}$")
    private String nickName;

    public SignupRequest(String email, String password, String userName, String nickName) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.nickName = nickName;
    }
}
