package org.zipli.socknet.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zipli.socknet.payload.request.SignupRequest;

import javax.validation.Valid;

import org.zipli.socknet.security.jwt.JwtUtils;
import org.zipli.socknet.security.services.UserDetailsImpl;
import reactor.core.publisher.Mono;
import org.springframework.web.server.ServerWebExchange;

@RestController
@RequestMapping(value = "/zipli/auth", consumes = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {
    private final JwtUtils jwtUtils;

    public AuthController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }


    @PostMapping("/signup")
    public ResponseEntity<?> addUser(@Valid @RequestBody SignupRequest signupRequest) {
        //method realization
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/signin")
    public Mono<ResponseEntity> login(ServerWebExchange swe) {
        return login(swe).defaultIfEmpty(ResponseEntity.ok("Here will be JwtResponse"));
    }
//    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
//        //method realization
//        return ResponseEntity.ok("Here will be JwtResponse");
//    }
}
