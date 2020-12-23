package org.zipli.socknet.service.auth;

import org.zipli.socknet.dto.UserData;
import org.zipli.socknet.model.User;

public interface IAuthService {
    UserData login(String email, String password);

    void registration(User user);
}
