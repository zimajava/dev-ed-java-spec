package org.zipli.socknet.service.auth;

import org.zipli.socknet.model.User;

public interface IAuthService {
    String login(String email, String password);

    void registration(User user);
}
