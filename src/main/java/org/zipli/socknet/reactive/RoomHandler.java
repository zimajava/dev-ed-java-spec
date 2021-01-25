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
import org.zipli.socknet.dto.response.DeleteRoomResponse;
import org.zipli.socknet.dto.response.ErrorResponse;
import org.zipli.socknet.dto.response.roomEvent.BaseEventResponse;
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
        String roomId = request.pathVariable("roomId");
        try {
            return serverResponseOk(roomService.getRoom(roomId));
        } catch (GetRoomException e) {
            log.error("Get Room fail: Room {} not exit", roomId);
            return serverResponseBadRequest(e.getErrorStatusCodeRoom());
        }
    }

    @Override
    public Mono<ServerResponse> getRooms(ServerRequest request) {
        return serverResponseOk(roomService.getRooms());
    }

    public Mono<ServerResponse> joinRoom(ServerRequest request) {
        String roomId = request.pathVariable("roomId");
        Optional<UserInfoByRoomRequest> userInfoByRoom = request.bodyToMono(UserInfoByRoomRequest.class).blockOptional();
        if (roomId != null) {
            if (userInfoByRoom.isPresent()) {
                try {
                    return serverResponseOk(roomService.joinRoom(roomId, userInfoByRoom.get()));
                } catch (JoinRoomException e) {
                    log.error("Join Room fail: Room {} not exit", roomId);
                    return serverResponseBadRequest(e.getErrorStatusCodeRoom());
                }
            } else {
                log.error("Join Room {} fail: INCORRECT_REQUEST, Body(UserInfoByRoomRequest) not exist.", roomId);
                return serverResponseBadRequest(ErrorStatusCode.INCORRECT_REQUEST);
            }
        } else {
            log.error("Join Room fail: INCORRECT_REQUEST, pathVariable roomId not exist.");
            return serverResponseBadRequest(ErrorStatusCode.INCORRECT_REQUEST);
        }
    }

    @Override
    public Mono<ServerResponse> leaveRoom(ServerRequest request) {
        String roomId = request.pathVariable("roomId");
        Optional<UserInfoByRoomRequest> userInfoByRoom = request.bodyToMono(UserInfoByRoomRequest.class).blockOptional();
        if (roomId != null) {
            if (userInfoByRoom.isPresent()) {
                try {
                    return serverResponseOk(roomService.leaveRoom(roomId, userInfoByRoom.get()));
                } catch (LiveRoomException e) {
                    if (e.getErrorStatusCodeRoom() == ErrorStatusCode.CHAT_NOT_EXISTS) {
                        log.error("Leave Room fail: Room {} not exit", roomId);
                    } else {
                        log.error("Leave Room {} fail: INCORRECT_REQUEST, Body(UserInfoByRoomRequest).getUserName not exist.", roomId);
                    }
                    return serverResponseBadRequest(e.getErrorStatusCodeRoom());
                }
            } else {
                log.error("Leave Room {} fail: INCORRECT_REQUEST, Body(UserInfoByRoomRequest) not exist.", roomId);
                return serverResponseBadRequest(ErrorStatusCode.INCORRECT_REQUEST);
            }
        } else {
            log.error("Join Room fail: INCORRECT_REQUEST, pathVariable roomId not exist.");
            return serverResponseBadRequest(ErrorStatusCode.INCORRECT_REQUEST);
        }
    }

    @Override
    public Mono<ServerResponse> deleteRoom(ServerRequest request) {
        String roomId = request.pathVariable("roomId");
        if (roomId != null) {
            try {
                roomService.deleteRoom(roomId);
                return serverResponseOk(new DeleteRoomResponse("Ok"));
            } catch (Exception e) {
                log.error("Delete Room fail: Room {} not exit", roomId);
                return serverResponseBadRequest(ErrorStatusCode.ROOM_NOT_EXIT);
            }
        } else {
            log.error("Join Room fail: INCORRECT_REQUEST, pathVariable roomId not exist.");
            return serverResponseBadRequest(ErrorStatusCode.INCORRECT_REQUEST);
        }
    }

    public Mono<ServerResponse> subscribeMessage(ServerRequest request) {
        String roomId = request.pathVariable("roomId");
        return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM)
                .body(BodyInserters.fromPublisher(roomService.subscribeMessage(roomId),
                        new ParameterizedTypeReference<ServerSentEvent<BaseEventResponse>>() {
                        }));
    }

    @Override
    public Mono<ServerResponse> getMessagesByRoom(ServerRequest request) {
        String roomId = request.pathVariable("roomId");
        if (roomId != null) {
            try {
                return serverResponseOk(roomService.getMessagesByRoom(roomId));
            } catch (GetMessagesByRoomException e) {
                log.error("Get Messages By Room fail: Room {} not exit", roomId);
                return serverResponseBadRequest(e.getErrorStatusCodeRoom());
            }
        } else {
            log.error("Join Room fail. INCORRECT_REQUEST: pathVariable roomId not exist.");
            return serverResponseBadRequest(ErrorStatusCode.INCORRECT_REQUEST);
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
                return serverResponseBadRequest(e.getErrorStatusCodeRoom());
            }
        } else {
            log.error("Create Room fail. INCORRECT_REQUEST: Body(CreateRoomRequest) not exist.");
            return serverResponseBadRequest(ErrorStatusCode.INCORRECT_REQUEST);
        }
    }

    public Mono<ServerResponse> saveMessage(ServerRequest request) {
        String roomId = request.pathVariable("roomId");
        Optional<MessageRoomRequest> message = request.bodyToMono(MessageRoomRequest.class).blockOptional();
        if (roomId != null) {
            if (message.isPresent()) {
                try {
                    return serverResponseOk(roomService.saveMessage(roomId, message.get()));
                } catch (SendMessageToRoomException e) {
                    log.error("Save message fail: Room {} not exit", roomId);
                    return serverResponseBadRequest(e.getErrorStatusCodeRoom());
                }
            } else {
                log.error("Create Room fail. INCORRECT_REQUEST: Body(MessageEventResponse) not exist.");
                return serverResponseBadRequest(ErrorStatusCode.INCORRECT_REQUEST);
            }
        } else {
            log.error("Join Room fail: INCORRECT_REQUEST, pathVariable roomId not exist.");
            return serverResponseBadRequest(ErrorStatusCode.INCORRECT_REQUEST);
        }
    }

    private Mono<ServerResponse> serverResponseBadRequest(ErrorStatusCode errorStatusCode) {
        return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new ErrorResponse(errorStatusCode)));
    }

    private Mono<ServerResponse> serverResponseOk(Object object) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(object));
    }

}
