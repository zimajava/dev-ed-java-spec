package org.zipli.socknet.service.auth;

import org.zipli.socknet.dto.response.LoginResponse;
import org.zipli.socknet.model.User;

public interface IAuthService {
    LoginResponse login(String email, String password);

    void registration(User user);
}
