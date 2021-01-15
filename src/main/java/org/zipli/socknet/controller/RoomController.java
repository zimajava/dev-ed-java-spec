package org.zipli.socknet.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zipli.socknet.exception.KeyChatNotFoundException;
import org.zipli.socknet.exception.auth.InvalidTokenException;
import org.zipli.socknet.model.Chat;
import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.service.room.RoomService;

@RestController
@Slf4j
@RequestMapping("/zipli/room")
public class RoomController {

    final JwtUtils jwtUtils;
    final RoomService roomService;

    public RoomController(JwtUtils jwtUtils, RoomService roomService) {
        this.jwtUtils = jwtUtils;
        this.roomService = roomService;
    }

    @GetMapping("/join/{keyRoom}")
    public ResponseEntity<?> getRoom(@PathVariable String keyRoom) {
        try {
            roomService.getIdChatByKey(keyRoom);
            return ResponseEntity.ok("");
        } catch (KeyChatNotFoundException e) {
            return ResponseEntity.badRequest().body("");
        }
    }

    @PostMapping("/join/{keyRoom}")
    public ResponseEntity<?> postRoom(@PathVariable String keyRoom,
                                      @RequestParam("token") String token) {
        try {
            Chat chat = roomService.joinRoom(keyRoom,token);
            return ResponseEntity.ok("");
        } catch (KeyChatNotFoundException e) {
            return ResponseEntity.badRequest().body("");
        } catch (InvalidTokenException e) {
            return ResponseEntity.badRequest().body("");
        }
    }
}
