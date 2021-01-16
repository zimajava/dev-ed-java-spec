package org.zipli.socknet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class MainController {

//    @Autowired
//    private WebClient webClient;

    @GetMapping("/")
    public Mono<String> index(@AuthenticationPrincipal Mono<OAuth2User> oauth2User) {
//        Map<String, Object> atrr = OAuth2User::getAttributes;
        return oauth2User/*.map(OAuth2User::getAttributes);*/
                .map(OAuth2User::getName)
                .map(name -> String.format("Hi, %s", name));

    }

}
