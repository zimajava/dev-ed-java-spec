package org.zipli.socknet.service.account;

import org.zipli.socknet.model.User;
import org.zipli.socknet.payload.request.MyAccountChange;

public interface IUserService {

    User findUser(String userID);

    User updateAvatar(MyAccountChange data);

    User updateNickName(MyAccountChange data);

    User updateEmail(MyAccountChange data);

    User updatePassword(MyAccountChange data);

}
