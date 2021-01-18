package org.zipli.socknet.reactive;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RoomRouter {

    @Bean
    public RouterFunction<ServerResponse> route(RoomHandler roomHandler) {

        final String PATH = "/zipli/room";
        return RouterFunctions
                .route(RequestPredicates
                                .PATCH(PATH + "/getRoom/{idRoom}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        roomHandler::getRoom)
                .andRoute(RequestPredicates
                                .POST(PATH + "/joinRoom/{idRoom}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        roomHandler::joinRoom)
                .andRoute(RequestPredicates
                                .POST(PATH + "/stream-sse{idRoom}")
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