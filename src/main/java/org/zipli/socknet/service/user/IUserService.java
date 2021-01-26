package org.zipli.socknet.service.user;

import org.zipli.socknet.dto.request.AvatarRequest;
import org.zipli.socknet.dto.request.EmailRequest;
import org.zipli.socknet.dto.request.NickNameRequest;
import org.zipli.socknet.dto.request.PasswordRequest;
import org.zipli.socknet.repository.model.User;

import javax.mail.MessagingException;

public interface IUserService {

    User findUser(String userId);

    User deleteAvatar(String userId);

    User updateAvatar(AvatarRequest data);

    User updateNickName(NickNameRequest data);

    User updateEmail(EmailRequest data) throws MessagingException;

    User updatePassword(PasswordRequest data);

    String deleteAccount(String userId);

}
