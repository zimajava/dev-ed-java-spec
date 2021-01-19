package org.zipli.socknet.reactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.zipli.socknet.dto.UserInfoByRoom;
import org.zipli.socknet.dto.room.BaseSseDto;
import org.zipli.socknet.dto.room.MessageSseDto;
import org.zipli.socknet.exception.ErrorStatusCodeRoom;
import org.zipli.socknet.exception.message.SendMessageException;
import org.zipli.socknet.exception.room.*;
import org.zipli.socknet.service.room.IRoomService;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Component
public class RoomHandler implements IRoomHandler {

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
            log.error("Get Room fail:");
            log.error(e.getMessage(), idRoom);
            return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(e.getErrorStatusCodeRoom().getNumberException()));
        }
    }

    @Override
    public Mono<ServerResponse> getRooms(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(roomService.getRooms()));
    }

    public Mono<ServerResponse> joinRoom(ServerRequest request) {
        String idRoom = request.pathVariable("idRoom");
        Optional<UserInfoByRoom> userInfoByRoom = request.bodyToMono(UserInfoByRoom.class).blockOptional();

        if (userInfoByRoom.isPresent()) {
            try {
                return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(BodyInserters.fromValue(roomService.joinRoom(idRoom, userInfoByRoom.get())));
            } catch (JoinRoomException e) {
                log.error("Join Room fail:");
                log.error(e.getMessage(), idRoom);
                return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(e.getErrorStatusCodeRoom().getNumberException()));
            }
        } else {
            return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(ErrorStatusCodeRoom.INCORRECT_REQUEST.getNumberException()));
        }
    }

    @Override
    public Mono<ServerResponse> leaveRoom(ServerRequest request) {
        String idRoom = request.pathVariable("idRoom");
        Optional<UserInfoByRoom> userInfoByRoom = request.bodyToMono(UserInfoByRoom.class).blockOptional();
        if (userInfoByRoom.isPresent()) {
            try {
                return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(BodyInserters.fromValue(roomService.leaveRoom(idRoom, userInfoByRoom.get())));
            } catch (LiveRoomException e) {
                log.error("Leave Room fail:");
                log.error(e.getMessage(), idRoom);
                return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(e.getErrorStatusCodeRoom().getNumberException()));
            }
        } else {
            return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(ErrorStatusCodeRoom.INCORRECT_REQUEST.getNumberException()));
        }
    }

    @Override
    public Mono<ServerResponse> deleteRoom(ServerRequest request) {
        String idRoom = request.pathVariable("idRoom");
        try {
            roomService.deleteRoom(idRoom);
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue("Ok"));
        } catch (Exception e) {
            return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(ErrorStatusCodeRoom.ROOM_NOT_EXIT.getNumberException()));
        }

    }

    public Mono<ServerResponse> subscribeMessage(ServerRequest request) {
        String idRoom = request.pathVariable("idRoom");
        return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM)
                .body(BodyInserters.fromPublisher(roomService.subscribeMessage(idRoom),
                        new ParameterizedTypeReference<ServerSentEvent<BaseSseDto>>() {
                        }));
    }

    @Override
    public Mono<ServerResponse> getMessagesByRoom(ServerRequest request) {
        String idRoom = request.pathVariable("idRoom");
        try {
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(roomService.getMessagesByRoom(idRoom)));
        } catch (GetMessagesByRoomException e) {
            log.error("Get Messages By Room fail:");
            log.error(e.getMessage(), idRoom);
            return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(e.getErrorStatusCodeRoom().getNumberException()));
        }
    }

    public Mono<ServerResponse> createRoom(ServerRequest request) {
        Optional<String> userName = request.queryParam("userName");
        Optional<String> chatName = request.queryParam("chatName");
        if (userName.isPresent() && chatName.isPresent()) {
            try {
                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(roomService.createRoom(userName.get(), chatName.get())));
            } catch (CreateRoomException e) {
                log.error("Create Room fail:");
                log.error(e.getMessage(), chatName);
                return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(e.getErrorStatusCodeRoom().getNumberException()));
            }
        } else {
            return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(ErrorStatusCodeRoom.INCORRECT_REQUEST.getNumberException()));
        }
    }

    public Mono<ServerResponse> saveMessage(ServerRequest request) {
        String idRoom = request.pathVariable("idRoom");
        Optional<MessageSseDto> message = request.bodyToMono(MessageSseDto.class).blockOptional();
        if (message.isPresent()) {
            try {
                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(roomService.saveMessage(idRoom, message.get())));
            } catch (SendMessageException e) {
                log.error("Save message fail:");
                log.error(e.getMessage(), idRoom);
                return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(ErrorStatusCodeRoom.ROOM_NOT_EXIT.getNumberException()));
            }
        } else {
            return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(ErrorStatusCodeRoom.INCORRECT_REQUEST.getNumberException()));
        }
    }
}
