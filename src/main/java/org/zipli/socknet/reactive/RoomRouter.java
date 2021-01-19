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

    private static final String PATH = "/zipli/room";

    @Bean
    public RouterFunction<ServerResponse> route(RoomHandler roomHandler) {

        return RouterFunctions
                .route(RequestPredicates
                                .GET(PATH + "/getRoom/{idRoom}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        roomHandler::getRoom)
                .andRoute(RequestPredicates
                                .POST(PATH + "/leaveRoom/{idRoom}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        roomHandler::leaveRoom)
                .andRoute(RequestPredicates
                                .POST(PATH + "/createRoom")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        roomHandler::createRoom)
                .andRoute(RequestPredicates
                                .GET(PATH + "/getRooms")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        roomHandler::getRooms)
                .andRoute(RequestPredicates
                                .POST(PATH + "/deleteRoom/{idRoom}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        roomHandler::deleteRoom)
                .andRoute(RequestPredicates
                                .POST(PATH + "/newMessage/{idRoom}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        roomHandler::saveMessage)
                .andRoute(RequestPredicates
                                .GET(PATH + "/subscribeMessage/{idRoom}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        roomHandler::subscribeMessage)
                .andRoute(RequestPredicates
                                .GET(PATH + "/getMessages/{idRoom}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        roomHandler::getMessagesByRoom)
                ;
    }
}