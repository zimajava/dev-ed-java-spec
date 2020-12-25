package org.zipli.socknet.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zipli.socknet.exception.GetUserExeption;
import org.zipli.socknet.exception.UpdateAvatarException;
import org.zipli.socknet.exception.UpdateEmailException;
import org.zipli.socknet.exception.UpdatePasswordExсeption;
import org.zipli.socknet.payload.request.MyAccountChange;
import org.zipli.socknet.service.account.UserService;

@RestController
@Slf4j
@RequestMapping("/zipli/auth/myAccount")
public class AccountController {

    @Autowired
    UserService userService;


    @GetMapping("/getUser")
    public ResponseEntity<?> getUser(@RequestParam String userId) throws GetUserExeption {
        try {
            return ResponseEntity.ok(userService.findUser(userId));
        } catch (GetUserExeption e) {
            return ResponseEntity
                    .badRequest()
                    .body(e);
        }
    }

    @PutMapping("/update-avatar")
    public ResponseEntity<?> updateAvatar(@RequestBody MyAccountChange data) throws UpdateAvatarException {
        try {
            return ResponseEntity.ok(userService.updateAvatar(data));
        } catch (UpdateAvatarException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e);
        }
    }

    @PutMapping("/update-nickName")
    public ResponseEntity<?> updateNickName(@RequestBody MyAccountChange data) throws UpdateAvatarException {
        try {
            return ResponseEntity.ok(userService.updateNickName(data));
        } catch (UpdateAvatarException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e);
        }

    }

    @PutMapping("/update-email")
    public ResponseEntity<?> updateEmail(@RequestBody MyAccountChange data) throws UpdateEmailException {
        try {
            return ResponseEntity.ok(userService.updateEmail(data));
        } catch (UpdateEmailException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e);
        }

    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody MyAccountChange data) throws UpdatePasswordExсeption {
        try {
            return ResponseEntity.ok(userService.updatePassword(data));
        } catch (UpdatePasswordExсeption e) {
            return ResponseEntity
                    .badRequest()
                    .body(e);
        }
    }
}
