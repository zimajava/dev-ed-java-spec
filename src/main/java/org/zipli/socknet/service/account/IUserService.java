package org.zipli.socknet.service.account;

import org.zipli.socknet.model.User;
import org.zipli.socknet.payload.request.MyAccountChange;

import javax.mail.MessagingException;

public interface IUserService {

    User findUser(String userID);

    User updateAvatar(MyAccountChange data);

    User updateNickName(MyAccountChange data);

    User updateEmail(MyAccountChange data) throws MessagingException;

    User updatePassword(MyAccountChange data);

}
