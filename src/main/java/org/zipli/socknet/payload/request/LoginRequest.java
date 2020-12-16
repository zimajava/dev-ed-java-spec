package org.zipli.socknet.payload.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String email;
    private String password;

}
