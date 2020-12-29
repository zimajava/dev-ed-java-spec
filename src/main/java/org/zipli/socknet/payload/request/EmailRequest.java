package org.zipli.socknet.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;

@Getter
@Setter
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    private String userId;

    @NotBlank(message = "Email can't be empty")
    @NotNull(message = "Email can't be null")
    @Email
    @Size(max = 50)
    @Pattern(regexp = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,}$")
    private String email;
}
