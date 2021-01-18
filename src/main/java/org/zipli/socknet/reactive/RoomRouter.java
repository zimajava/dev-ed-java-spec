package org.zipli.socknet.reactive;

import com.sun.mail.iap.Response;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.zipli.socknet.dto.WsMessageResponse;

@Configuration
public class RoomRouter {

    private static final String PATH = "/zipli/room";

    @Bean
    public RouterFunction<ServerResponse> route(RoomHandler roomHandler) {

        return RouterFunctions
                .route(RequestPredicates
                                .GET(PATH + "/getRoom/{idRoom}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        roomHandler::getRoom)
                .andRoute(RequestPredicates
                                .POST(PATH + "/joinRoom/{idRoom}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        roomHandler::joinRoom)
                .andRoute(RequestPredicates
                                .GET(PATH + "/subscribeMessage/{idRoom}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        roomHandler::getMessage)
                .andRoute(RequestPredicates
                                .POST(PATH + "/createRoom")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        roomHandler::createRoom)
                .andRoute(RequestPredicates
                                .POST(PATH + "/new-massege")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        roomHandler::saveMessage);
    }
}