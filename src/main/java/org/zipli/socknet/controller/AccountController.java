package org.zipli.socknet.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zipli.socknet.exception.*;
import org.zipli.socknet.exception.account.*;
import org.zipli.socknet.payload.request.AvatarRequest;
import org.zipli.socknet.payload.request.EmailRequest;
import org.zipli.socknet.payload.request.NickNameRequest;
import org.zipli.socknet.payload.request.PasswordRequest;
import org.zipli.socknet.service.account.UserService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/zipli/myAccount")
public class AccountController {

    private final UserService userService;

    @Autowired
    public AccountController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/getUser")
    public ResponseEntity<?> getUser(@RequestParam String userId) {
        try {
            return ResponseEntity.ok(userService.findUser(userId));
        } catch (GetUserException e) {
            log.error(e.getErrorStatusCode().getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getErrorStatusCode().getValue());
        }
    }

    @PutMapping("/delete-avatar")
    public ResponseEntity<?> deleteAvatar(@RequestParam String userId) {
        try {
            return ResponseEntity.ok(userService.deleteAvatar(userId));
        } catch (DeleteAvatarException e) {
            log.error(e.getErrorStatusCode().getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getErrorStatusCode().getValue());
        }
    }

    @PutMapping("/update-avatar")
    public ResponseEntity<?> updateAvatar(@RequestBody @Valid AvatarRequest data) {
        try {
            return ResponseEntity.ok(userService.updateAvatar(data));
        } catch (UpdateAvatarException e) {
            log.error(e.getErrorStatusCode().getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getErrorStatusCode().getValue());
        }
    }

    @PutMapping("/update-nickName")
    public ResponseEntity<?> updateNickName(@RequestBody @Valid NickNameRequest data) {
        try {
            return ResponseEntity.ok(userService.updateNickName(data));
        } catch (UpdateNickNameException e) {
            log.error(e.getErrorStatusCode().getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getErrorStatusCode().getValue());
        }
    }

    @PutMapping("/update-email")
    public ResponseEntity<?> updateEmail(@RequestBody @Valid EmailRequest data) {
        try {
            return ResponseEntity.ok(userService.updateEmail(data));
        } catch (UpdateEmailException e) {
            log.error(e.getErrorStatusCode().getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getErrorStatusCode().getValue());
        }
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid PasswordRequest data) {
        try {
            return ResponseEntity.ok(userService.updatePassword(data));
        } catch (UpdatePasswordException e) {
            log.error(e.getErrorStatusCode().getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getErrorStatusCode().getValue());
        }
    }

    @PostMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(@RequestParam String userId) {
        try {
            return ResponseEntity.ok(userService.deleteAccount(userId));
        } catch (DeleteAccountException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e);
        }
    }
}
