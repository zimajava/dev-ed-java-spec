package org.zipli.socknet.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Login can't be empty")
    @NotNull
    @Pattern(regexp = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,}$|^[a-zA-Z_-]{2,16}$")
    private String login;

    @NotBlank(message = "Password can't be empty")
    @NotNull
    @Size(min = 8, max = 16)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,16}$")
    private String password;
}
