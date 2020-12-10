package org.zipli.socknet.payload.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Validated
public class LoginRequest {
    @NotBlank(message = "Login can't be empty")
    private String login;

    @NotBlank(message = "Password can't be empty")
    @Size(min = 8, max = 16)
    private String password;

    public LoginRequest(@NotBlank String login, @NotBlank String password) {
        this.login = login;
        this.password = password;
    }
}
