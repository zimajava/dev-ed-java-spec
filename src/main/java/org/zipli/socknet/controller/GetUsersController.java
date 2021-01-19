package org.zipli.socknet.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zipli.socknet.exception.chat.GetAllUsersException;
import org.zipli.socknet.service.chat.GetUsersService;

@RestController
@Slf4j
@RequestMapping("/zipli/сhat")
public class GetUsersController {

    private final GetUsersService chatService;

    public GetUsersController(GetUsersService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(chatService.getAllUsers());
        } catch (GetAllUsersException e) {
            log.error(e.getErrorStatusCode().getMessage(), "Failed get users");
            return ResponseEntity
                    .badRequest()
                    .body(e.getErrorStatusCode().getValue());
        }
    }
}
