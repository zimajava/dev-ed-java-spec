package org.zipli.socknet.reactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.zipli.socknet.dto.UserInfoByRoom;
import org.zipli.socknet.exception.ErrorStatusCodeRoom;
import org.zipli.socknet.exception.SendMessageException;
import org.zipli.socknet.exception.room.CreateRoomException;
import org.zipli.socknet.exception.room.GetRoomException;
import org.zipli.socknet.exception.room.JoinRoomException;
import org.zipli.socknet.model.Message;
import org.zipli.socknet.service.room.IRoomService;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Component
public class RoomHandler {

    final IRoomService roomService;

    public RoomHandler(IRoomService roomService) {
        this.roomService = roomService;
    }

    public Mono<ServerResponse> getRoom(ServerRequest request) {
        String idRoom = request.pathVariable("idRoom");
        try {
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(roomService.getRoom(idRoom)));
        } catch (GetRoomException e) {
            log.error(e.getMessage(), idRoom);
            return ServerResponse.badRequest()
                    .body(BodyInserters.fromValue(ErrorStatusCodeRoom.ROOM_NOT_EXIT.getNumberException()));
        }
    }

    public Mono<ServerResponse> joinRoom(ServerRequest request) {
        String idRoom = request.pathVariable("idRoom");
        Optional<String> signal = request.queryParam("signal");
        Mono<UserInfoByRoom> userInfoByRoomMono = request.bodyToMono(UserInfoByRoom.class);
        Optional<UserInfoByRoom> userInfoByRoom = userInfoByRoomMono.blockOptional();

        if (signal.isPresent() && userInfoByRoom.isPresent()) {
            try {
                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(roomService.joinRoom(idRoom, userInfoByRoom.get(), signal.get())));
            } catch (JoinRoomException e) {
                log.error(e.getMessage(), idRoom);
                return ServerResponse.badRequest()
                        .body(BodyInserters.fromValue(ErrorStatusCodeRoom.ROOM_NOT_EXIT.getNumberException()));
            }
        } else {
            return ServerResponse.badRequest()
                    .body(BodyInserters.fromValue(ErrorStatusCodeRoom.INCORRECT_REQUEST.getNumberException()));
        }
    }

    public Mono<ServerResponse> getMessage(ServerRequest request) {
        String idRoom = request.pathVariable("idRoom");
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(roomService.getMessage(idRoom)));
    }

    public Mono<ServerResponse> createRoom(ServerRequest request) {
        Optional<String> idUser = request.queryParam("idUser");
        Optional<String> chatName = request.queryParam("chatName");
        if (idUser.isPresent() && chatName.isPresent()) {
            try {
                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(roomService.createRoom(idUser.get(), chatName.get())));
            } catch (CreateRoomException e) {
                log.error(e.getMessage(), chatName);
                return ServerResponse.badRequest()
                        .body(BodyInserters.fromValue(ErrorStatusCodeRoom.ROOM_NOT_EXIT.getNumberException()));
            }
        } else {
            return ServerResponse.badRequest()
                    .body(BodyInserters.fromValue(ErrorStatusCodeRoom.INCORRECT_REQUEST.getNumberException()));
        }
    }

    public Mono<ServerResponse> saveMessage(ServerRequest request) {
        Mono<Message> messageMono = request.bodyToMono(Message.class);
        String idRoom = request.pathVariable("idRoom");
        Optional<Message> message = messageMono.blockOptional();
        if (message.isPresent()) {
            try {
                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(roomService.saveMessage(message.get(), idRoom)));
            } catch (SendMessageException e) {
                log.error(e.getMessage(), idRoom);
                return ServerResponse.badRequest()
                        .body(BodyInserters.fromValue(ErrorStatusCodeRoom.ROOM_NOT_EXIT.getNumberException()));
            }
        } else {
            return ServerResponse.badRequest()
                    .body(BodyInserters.fromValue(ErrorStatusCodeRoom.INCORRECT_REQUEST.getNumberException()));
        }
    }
}
