package org.zipli.socknet.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class NickNameRequest {
    private String userId;

    @NotBlank(message = "Nickname can't be empty")
    @Pattern(regexp = "^.{1,16}$")
    private String nickName;
}
