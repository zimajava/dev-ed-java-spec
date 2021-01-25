package org.zipli.socknet.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zipli.socknet.dto.response.UserInfo;
import org.zipli.socknet.exception.account.DeleteAvatarException;
import org.zipli.socknet.exception.account.UpdateAvatarException;
import org.zipli.socknet.exception.account.UpdateEmailException;
import org.zipli.socknet.exception.account.UpdateNickNameException;
import org.zipli.socknet.dto.request.AvatarRequest;
import org.zipli.socknet.dto.request.EmailRequest;
import org.zipli.socknet.dto.request.NickNameRequest;
import org.zipli.socknet.dto.request.PasswordRequest;
import org.zipli.socknet.exception.account.*;
import org.zipli.socknet.service.user.UserService;

import javax.validation.Valid;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/zipli/myAccount")
public class AccountController {
    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getUser/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(new UserInfo(userService.findUser(userId)));
        } catch (GetUserException e) {
            log.error("Failed get user by userId {}, reason {}", userId, e.getErrorStatusCode().getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getErrorStatusCode().getValue());
        }
    }

    @DeleteMapping("/delete-avatar/{userId}")
    public ResponseEntity<?> deleteAvatar(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(new UserInfo(userService.deleteAvatar(userId)));
        } catch (DeleteAvatarException e) {
            log.error("Failed to delete avatar by userId {}, reason {}", userId, e.getErrorStatusCode().getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getErrorStatusCode().getValue());
        }
    }

    @PutMapping("/update-avatar")
    public ResponseEntity<?> updateAvatar(@RequestBody @Valid AvatarRequest data) {
        try {
            return ResponseEntity.ok(new UserInfo(userService.updateAvatar(data)));
        } catch (UpdateAvatarException e) {
            log.error("Failed update avatar by userId {}, avatarIsNull {}, reason {}",
                    data.getUserId(), Objects.isNull(data.getAvatar()), e.getErrorStatusCode().getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getErrorStatusCode().getValue());
        }
    }

    @PutMapping("/update-nickName")
    public ResponseEntity<?> updateNickName(@RequestBody @Valid NickNameRequest data) {
        try {
            return ResponseEntity.ok(new UserInfo(userService.updateNickName(data)));
        } catch (UpdateNickNameException e) {
            log.error("Failed update nickName by userId {}, nickName {}, reason {}",
                    data.getUserId(), data.getNickName(), e.getErrorStatusCode().getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getErrorStatusCode().getValue());
        }
    }

    @PutMapping("/update-email")
    public ResponseEntity<?> updateEmail(@RequestBody @Valid EmailRequest data) {
        try {
            return ResponseEntity.ok(new UserInfo(userService.updateEmail(data)));
        } catch (UpdateEmailException e) {
            log.error("Failed update email by userId {}, email {}, reason {}",
                    data.getUserId(), data.getEmail(), e.getErrorStatusCode().getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getErrorStatusCode().getValue());
        }
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid PasswordRequest data) {
        try {
            return ResponseEntity.ok(new UserInfo(userService.updatePassword(data)));
        } catch (UpdatePasswordException e) {
            log.error("Failed update password by userId {}, passwordIsNull {}, reason {}",
                    data.getUserId(), Objects.isNull(data.getPassword()), e.getErrorStatusCode().getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getErrorStatusCode().getValue());
        }
    }

    @DeleteMapping("/delete-account/{userId}")
    public ResponseEntity<?> deleteAccount(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(userService.deleteAccount(userId));
        } catch (DeleteAccountException e) {
            log.error("Failed delete user by userId {}, reason {}", userId, e.getErrorStatusCode().getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getErrorStatusCode().getValue());
        }
    }
}
