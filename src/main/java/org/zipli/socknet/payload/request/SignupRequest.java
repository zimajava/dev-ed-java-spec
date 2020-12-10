package org.zipli.socknet.payload.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Language;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SignupRequest {
    @NotBlank(message = "Email can't be empty")
    @Email
    @Size(max = 50)
    @Pattern(regexp = "")
    private String email;

    @NotBlank(message = "Password can't be empty")
    @Size(min = 8, max = 16)
    private String password;

    @NotBlank(message = "User name can't be empty")
    private String userName;

    @NotBlank(message = "Nickname can't be empty")
    private String nickName;

    public SignupRequest(String email, String password, String userName, String nickName) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.nickName = nickName;
    }
}
