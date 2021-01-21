package org.zipli.socknet.reactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.zipli.socknet.dto.UserInfoByRoomResponse;
import org.zipli.socknet.dto.response.BaseEventResponse;
import org.zipli.socknet.dto.response.MessageEventResponse;
import org.zipli.socknet.exception.ErrorStatusCodeRoom;
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
            return serverResponseOk(roomService.getRoom(idRoom));
        } catch (GetRoomException e) {
            log.error("Get Room fail: Room {} not exit", idRoom);
            return serverResponseBadRequest(e.getErrorStatusCodeRoom().getNumberException());
        }
    }

    @Override
    public Mono<ServerResponse> getRooms(ServerRequest request) {
        return serverResponseOk(roomService.getRooms());
    }

    public Mono<ServerResponse> joinRoom(ServerRequest request) {
        String idRoom = request.pathVariable("idRoom");
        Optional<UserInfoByRoomResponse> userInfoByRoom = request.bodyToMono(UserInfoByRoomResponse.class).blockOptional();

        if (userInfoByRoom.isPresent()) {
            try {
                return serverResponseOk(roomService.joinRoom(idRoom, userInfoByRoom.get()));
            } catch (JoinRoomException e) {
                log.error("Join Room fail: Room {} not exit", idRoom);
                return serverResponseBadRequest(e.getErrorStatusCodeRoom().getNumberException());
            }
        } else {
            log.error("Join Room {} fail: INCORRECT_REQUEST", idRoom);
            return serverResponseBadRequest(ErrorStatusCodeRoom.INCORRECT_REQUEST.getNumberException());
        }
    }

    @Override
    public Mono<ServerResponse> leaveRoom(ServerRequest request) {
        String idRoom = request.pathVariable("idRoom");
        Optional<UserInfoByRoomResponse> userInfoByRoom = request.bodyToMono(UserInfoByRoomResponse.class).blockOptional();
        if (userInfoByRoom.isPresent()) {
            try {
                return serverResponseOk(roomService.leaveRoom(idRoom, userInfoByRoom.get()));
            } catch (LiveRoomException e) {
                log.error("Leave Room fail: Room {} not exit", idRoom);
                return serverResponseBadRequest(e.getErrorStatusCodeRoom().getNumberException());
            }
        } else {
            log.error("Leave Room {} fail: INCORRECT_REQUEST", idRoom);
            return serverResponseBadRequest(ErrorStatusCodeRoom.INCORRECT_REQUEST.getNumberException());
        }
    }

    @Override
    public Mono<ServerResponse> deleteRoom(ServerRequest request) {
        String idRoom = request.pathVariable("idRoom");
        try {
            roomService.deleteRoom(idRoom);
            return serverResponseOk("OK");
        } catch (Exception e) {
            log.error("Delete Room fail: Room {} not exit", idRoom);
            return serverResponseBadRequest(ErrorStatusCodeRoom.ROOM_NOT_EXIT.getNumberException());
        }

    }

    public Mono<ServerResponse> subscribeMessage(ServerRequest request) {
        String idRoom = request.pathVariable("idRoom");
        return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM)
                .body(BodyInserters.fromPublisher(roomService.subscribeMessage(idRoom),
                        new ParameterizedTypeReference<ServerSentEvent<BaseEventResponse>>() {
                        }));
    }

    @Override
    public Mono<ServerResponse> getMessagesByRoom(ServerRequest request) {
        String idRoom = request.pathVariable("idRoom");
        try {
            return serverResponseOk(roomService.getMessagesByRoom(idRoom));
        } catch (GetMessagesByRoomException e) {
            log.error("Get Messages By Room fail: Room {} not exit", idRoom);
            return serverResponseBadRequest(e.getErrorStatusCodeRoom().getNumberException());
        }
    }

    public Mono<ServerResponse> createRoom(ServerRequest request) {
        Optional<String> userName = request.queryParam("userName");
        Optional<String> chatName = request.queryParam("chatName");
        if (userName.isPresent() && chatName.isPresent()) {
            try {
                return serverResponseOk(roomService.createRoom(userName.get(), chatName.get()));
            } catch (CreateRoomException e) {
                log.error("Create Room fail. ROOM_ALREADY_EXISTS: Chat Name - {}", chatName.get());
                return serverResponseBadRequest(e.getErrorStatusCodeRoom().getNumberException());
            }
        } else {
            log.error("Create Room fail. INCORRECT_REQUEST: User Name - {}, Chat Name - {}", userName.get(), chatName.get());
            return serverResponseBadRequest(ErrorStatusCodeRoom.INCORRECT_REQUEST.getNumberException());
        }
    }

    public Mono<ServerResponse> saveMessage(ServerRequest request) {
        String idRoom = request.pathVariable("idRoom");
        Optional<MessageEventResponse> message = request.bodyToMono(MessageEventResponse.class).blockOptional();
        if (message.isPresent()) {
            try {
                return serverResponseOk(roomService.saveMessage(idRoom, message.get()));
            } catch (SendMessageToRoomException e) {
                log.error("Save message fail: Room {} not exit", idRoom);
                return serverResponseBadRequest(e.getErrorStatusCodeRoom().getNumberException());
            }
        } else {
            log.error("Create Room fail. INCORRECT_REQUEST: MessageEventResponse - {}", message.get());
            return serverResponseBadRequest(ErrorStatusCodeRoom.INCORRECT_REQUEST.getNumberException());
        }
    }

    private Mono<ServerResponse> serverResponseBadRequest(Object object) {
        return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(object));
    }

    private Mono<ServerResponse> serverResponseOk(Object object) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(object));
    }

}
