package org.zipli.socknet.reactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.zipli.socknet.dto.request.CreateRoomRequest;
import org.zipli.socknet.dto.request.MessageRoomRequest;
import org.zipli.socknet.dto.request.UserInfoByRoomRequest;
import org.zipli.socknet.dto.response.BaseEventResponse;
import org.zipli.socknet.dto.response.MessageEventResponse;
import org.zipli.socknet.exception.ErrorStatusCode;
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
            return serverResponseBadRequest(e.getErrorStatusCodeRoom().getValue());
        }
    }

    @Override
    public Mono<ServerResponse> getRooms(ServerRequest request) {
        return serverResponseOk(roomService.getRooms());
    }

    public Mono<ServerResponse> joinRoom(ServerRequest request) {
        String idRoom = request.pathVariable("idRoom");
        Optional<UserInfoByRoomRequest> userInfoByRoom = request.bodyToMono(UserInfoByRoomRequest.class).blockOptional();

        if (userInfoByRoom.isPresent()) {
            try {
                return serverResponseOk(roomService.joinRoom(idRoom, userInfoByRoom.get()));
            } catch (JoinRoomException e) {
                log.error("Join Room fail: Room {} not exit", idRoom);
                return serverResponseBadRequest(e.getErrorStatusCodeRoom().getValue());
            }
        } else {
            log.error("Join Room {} fail: INCORRECT_REQUEST", idRoom);
            return serverResponseBadRequest(ErrorStatusCode.INCORRECT_REQUEST.getValue());
        }
    }

    @Override
    public Mono<ServerResponse> leaveRoom(ServerRequest request) {
        String idRoom = request.pathVariable("idRoom");
        Optional<UserInfoByRoomRequest> userInfoByRoom = request.bodyToMono(UserInfoByRoomRequest.class).blockOptional();
        if (userInfoByRoom.isPresent()) {
            try {
                return serverResponseOk(roomService.leaveRoom(idRoom, userInfoByRoom.get()));
            } catch (LiveRoomException e) {
                log.error("Leave Room fail: Room {} not exit", idRoom);
                return serverResponseBadRequest(e.getErrorStatusCodeRoom().getValue());
            }
        } else {
            log.error("Leave Room {} fail: INCORRECT_REQUEST", idRoom);
            return serverResponseBadRequest(ErrorStatusCode.INCORRECT_REQUEST.getValue());
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
            return serverResponseBadRequest(ErrorStatusCode.ROOM_NOT_EXIT.getValue());
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
            return serverResponseBadRequest(e.getErrorStatusCodeRoom().getValue());
        }
    }

    public Mono<ServerResponse> createRoom(ServerRequest request) {
        Optional<CreateRoomRequest> createRoomRequestOptional = request.bodyToMono(CreateRoomRequest.class).blockOptional();
        if (createRoomRequestOptional.isPresent()) {
            CreateRoomRequest createRoomRequest = createRoomRequestOptional.get();
            try {
                return serverResponseOk(roomService.createRoom(createRoomRequest.getUserName(), createRoomRequest.getChatName()));
            } catch (CreateRoomException e) {
                log.error("Create Room fail. ROOM_ALREADY_EXISTS: Chat Name - {}", createRoomRequest.getChatName());
                return serverResponseBadRequest(e.getErrorStatusCodeRoom().getValue());
            }
        } else {
            log.error("Create Room fail. INCORRECT_REQUEST: CreateRoomRequest - null");
            return serverResponseBadRequest(ErrorStatusCode.INCORRECT_REQUEST.getValue());
        }
    }

    public Mono<ServerResponse> saveMessage(ServerRequest request) {
        String idRoom = request.pathVariable("idRoom");
        Optional<MessageRoomRequest> message = request.bodyToMono(MessageRoomRequest.class).blockOptional();
        if (message.isPresent()) {
            try {
                return serverResponseOk(roomService.saveMessage(idRoom, message.get()));
            } catch (SendMessageToRoomException e) {
                log.error("Save message fail: Room {} not exit", idRoom);
                return serverResponseBadRequest(e.getErrorStatusCodeRoom().getValue());
            }
        } else {
            log.error("Create Room fail. INCORRECT_REQUEST: MessageEventResponse - null");
            return serverResponseBadRequest(ErrorStatusCode.INCORRECT_REQUEST.getValue());
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
