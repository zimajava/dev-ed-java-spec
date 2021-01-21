package org.zipli.socknet.reactive;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.zipli.socknet.dto.RoomsResponse;
import org.zipli.socknet.dto.UserInfoByRoomResponse;
import org.zipli.socknet.dto.response.BaseEventResponse;
import org.zipli.socknet.exception.ErrorStatusCodeRoom;
import org.zipli.socknet.exception.room.GetRoomException;
import org.zipli.socknet.exception.room.JoinRoomException;
import org.zipli.socknet.exception.room.LiveRoomException;
import org.zipli.socknet.model.Room;
import org.zipli.socknet.repository.RoomRepository;
import org.zipli.socknet.service.room.RoomService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
class RoomHandlerTest {

    @Autowired
    private RoomHandler roomHandler;

    @MockBean
    private RoomService roomService;
    @MockBean
    private ServerRequest request;

    private final Room room = new Room("RoomName", "CreatorUser");
    private final String idRoom = "";
    private List<RoomsResponse> roomsResponses;
    private final UserInfoByRoomResponse userInfoByRoomResponse = new UserInfoByRoomResponse("", "", "", false);

    @BeforeEach
    void setUp() {
        roomsResponses = new ArrayList<>();
        roomsResponses.add(new RoomsResponse("123", "RoomName"));
    }

    @Test
    void getRoom_Pass() throws GetRoomException {
        Mockito.when(request.pathVariable("idRoom")).thenReturn(idRoom);
        Mockito.when(roomService.getRoom(idRoom)).thenReturn(room);
        Mono<ServerResponse> serverResponseGetRoom = roomHandler.getRoom(request);

//        assertEquals(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(room)).toString(), serverResponseGetRoom.toString());
        assertEquals(Objects.requireNonNull(serverResponseGetRoom.block()).statusCode(), HttpStatus.OK);
    }

    @Test
    void getRoom_Fail() throws GetRoomException {
        Mockito.when(roomService.getRoom(null)).thenThrow(new GetRoomException(ErrorStatusCodeRoom.ROOM_NOT_EXIT));
        Mono<ServerResponse> serverResponseGetRoom = roomHandler.getRoom(request);

//        assertEquals(ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(ErrorStatusCodeRoom.ROOM_NOT_EXIT)).toString(), serverResponseGetRoom.toString());
        assertEquals(Objects.requireNonNull(serverResponseGetRoom.block()).statusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void getRooms() {
        Mockito.when(roomService.getRooms()).thenReturn(roomsResponses);
        Mono<ServerResponse> serverResponseGetRooms = roomHandler.getRooms(request);

//        assertEquals(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(roomsResponses)).toString(), serverResponseGetRooms.toString());
        assertEquals(Objects.requireNonNull(serverResponseGetRooms.block()).statusCode(), HttpStatus.OK);

    }

    @Test
    void joinRoom_Pass() throws JoinRoomException {
        Mockito.when(request.pathVariable("idRoom")).thenReturn(idRoom);
        Mockito.when(request.bodyToMono(UserInfoByRoomResponse.class)).thenReturn(Mono.just(userInfoByRoomResponse));
        Mockito.when(roomService.joinRoom(idRoom, userInfoByRoomResponse)).thenReturn(room);
        Mono<ServerResponse> serverResponseJoinRoom = roomHandler.joinRoom(request);

//        assertEquals(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(room)).toString(), serverResponseJoinRoom.toString());
        assertEquals(Objects.requireNonNull(serverResponseJoinRoom.block()).statusCode(), HttpStatus.OK);
    }

    @Test
    void joinRoom_Fail_INCORRECT_REQUEST() {
        Mockito.when(request.bodyToMono(UserInfoByRoomResponse.class)).thenReturn(Mono.empty());
        Mono<ServerResponse> serverResponseJoinRoom = roomHandler.joinRoom(request);

//        assertEquals(ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(ErrorStatusCodeRoom.INCORRECT_REQUEST)).toString(), serverResponseJoinRoom.toString());
        assertEquals(Objects.requireNonNull(serverResponseJoinRoom.block()).statusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void joinRoom_Fail_ROOM_NOT_EXIT() throws JoinRoomException {
        Mockito.when(request.bodyToMono(UserInfoByRoomResponse.class)).thenReturn(Mono.just(userInfoByRoomResponse));
        Mockito.when(request.pathVariable("idRoom")).thenReturn(idRoom);
        Mockito.when(roomService.joinRoom(idRoom, userInfoByRoomResponse))
                .thenThrow(new JoinRoomException(ErrorStatusCodeRoom.ROOM_NOT_EXIT));
        Mono<ServerResponse> serverResponseJoinRoom = roomHandler.joinRoom(request);

//        assertEquals(ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(ErrorStatusCodeRoom.ROOM_NOT_EXIT)).toString(), serverResponseJoinRoom.toString());
        assertEquals(Objects.requireNonNull(serverResponseJoinRoom.block()).statusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void leaveRoom_Pass() throws LiveRoomException {
        Mockito.when(request.pathVariable("idRoom")).thenReturn(idRoom);
        Mockito.when(request.bodyToMono(UserInfoByRoomResponse.class)).thenReturn(Mono.just(userInfoByRoomResponse));
        Mockito.when(roomService.leaveRoom(idRoom, userInfoByRoomResponse)).thenReturn(room);
        Mono<ServerResponse> serverResponseLeaveRoom = roomHandler.leaveRoom(request);

//        assertEquals(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(room)).toString(), serverResponseLeaveRoom.toString());
        assertEquals(Objects.requireNonNull(serverResponseLeaveRoom.block()).statusCode(), HttpStatus.OK);
    }

    @Test
    void leaveRoom_Fail_INCORRECT_REQUEST() {
        Mockito.when(request.bodyToMono(UserInfoByRoomResponse.class)).thenReturn(Mono.empty());
        Mono<ServerResponse> serverResponseLeaveRoom = roomHandler.leaveRoom(request);

//        assertEquals(ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(ErrorStatusCodeRoom.INCORRECT_REQUEST)).toString(), serverResponseLeaveRoom.toString());
        assertEquals(Objects.requireNonNull(serverResponseLeaveRoom.block()).statusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void leaveRoom_Fail_ROOM_NOT_EXIT() throws LiveRoomException {
        Mockito.when(request.bodyToMono(UserInfoByRoomResponse.class)).thenReturn(Mono.just(userInfoByRoomResponse));
        Mockito.when(request.pathVariable("idRoom")).thenReturn(idRoom);
        Mockito.when(roomService.leaveRoom(idRoom, userInfoByRoomResponse))
                .thenThrow(new LiveRoomException(ErrorStatusCodeRoom.ROOM_NOT_EXIT));
        Mono<ServerResponse> serverResponseLeaveRoom = roomHandler.leaveRoom(request);

//        assertEquals(ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(ErrorStatusCodeRoom.ROOM_NOT_EXIT)).toString(), serverResponseLeaveRoom.toString());
        assertEquals(Objects.requireNonNull(serverResponseLeaveRoom.block()).statusCode(), HttpStatus.BAD_REQUEST);
    }


    @Test
    void deleteRoom_Pass() {
        Mockito.when(request.pathVariable("idRoom")).thenReturn(idRoom);
        Mono<ServerResponse> serverResponseDeleteRoom = roomHandler.deleteRoom(request);

//        assertEquals(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue("OK")).toString(), serverResponseDeleteRoom.toString());
        assertEquals(Objects.requireNonNull(serverResponseDeleteRoom.block()).statusCode(), HttpStatus.OK);
    }

    @Test
    void getMessagesByRoom() {
    }

    @Test
    void createRoom() {
    }

    @Test
    void saveMessage() {
    }
}