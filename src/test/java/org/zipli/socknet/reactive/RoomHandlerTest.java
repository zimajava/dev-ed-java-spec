package org.zipli.socknet.reactive;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.zipli.socknet.dto.MessageRoom;
import org.zipli.socknet.dto.request.CreateRoomRequest;
import org.zipli.socknet.dto.request.MessageRoomRequest;
import org.zipli.socknet.dto.request.UserInfoByRoomRequest;
import org.zipli.socknet.dto.response.MessageEventResponse;
import org.zipli.socknet.dto.response.RoomsResponse;
import org.zipli.socknet.exception.ErrorStatusCode;
import org.zipli.socknet.exception.room.*;
import org.zipli.socknet.model.Room;
import org.zipli.socknet.service.room.RoomService;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
class RoomHandlerTest {

    private final Room room = new Room("RoomName", "CreatorUser");
    private final String idRoom = "";
    private final UserInfoByRoomRequest userInfoByRoomRequest =
            new UserInfoByRoomRequest("", "", "", false);
    private final CreateRoomRequest createRoomRequest = new CreateRoomRequest("UserName", "ChatName");
    private final MessageRoom messageRoom =
            new MessageRoom("AuthorUser", "IdRoom", "Text", new Date());
    private final MessageEventResponse messageEventResponse =
            new MessageEventResponse();
    @Autowired
    private RoomHandler roomHandler;
    @MockBean
    private RoomService roomService;
    @MockBean
    private ServerRequest request;
    private List<RoomsResponse> roomsResponses;

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
        //log.info(serverResponseGetRoom.i);
//        assertEquals(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(room)).toString(), serverResponseGetRoom.toString());
        assertEquals(Objects.requireNonNull(serverResponseGetRoom.block()).statusCode(), HttpStatus.OK);
    }

    @Test
    void getRoom_Fail() throws GetRoomException {
        Mockito.when(roomService.getRoom(null)).thenThrow(new GetRoomException(ErrorStatusCode.ROOM_NOT_EXIT));
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
        Mockito.when(request.bodyToMono(UserInfoByRoomRequest.class)).thenReturn(Mono.just(userInfoByRoomRequest));
        Mockito.when(roomService.joinRoom(idRoom, userInfoByRoomRequest)).thenReturn(room);
        Mono<ServerResponse> serverResponseJoinRoom = roomHandler.joinRoom(request);

//        assertEquals(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(room)).toString(), serverResponseJoinRoom.toString());
        assertEquals(Objects.requireNonNull(serverResponseJoinRoom.block()).statusCode(), HttpStatus.OK);
    }

    @Test
    void joinRoom_Fail_INCORRECT_REQUEST() {
        Mockito.when(request.bodyToMono(UserInfoByRoomRequest.class)).thenReturn(Mono.empty());
        Mono<ServerResponse> serverResponseJoinRoom = roomHandler.joinRoom(request);

//        assertEquals(ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(ErrorStatusCodeRoom.INCORRECT_REQUEST)).toString(), serverResponseJoinRoom.toString());
        assertEquals(Objects.requireNonNull(serverResponseJoinRoom.block()).statusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void joinRoom_Fail_ROOM_NOT_EXIT() throws JoinRoomException {
        Mockito.when(request.bodyToMono(UserInfoByRoomRequest.class)).thenReturn(Mono.just(userInfoByRoomRequest));
        Mockito.when(request.pathVariable("idRoom")).thenReturn(idRoom);
        Mockito.when(roomService.joinRoom(idRoom, userInfoByRoomRequest))
                .thenThrow(new JoinRoomException(ErrorStatusCode.ROOM_NOT_EXIT));
        Mono<ServerResponse> serverResponseJoinRoom = roomHandler.joinRoom(request);

//        assertEquals(ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(ErrorStatusCodeRoom.ROOM_NOT_EXIT)).toString(), serverResponseJoinRoom.toString());
        assertEquals(Objects.requireNonNull(serverResponseJoinRoom.block()).statusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void leaveRoom_Pass() throws LiveRoomException {
        Mockito.when(request.pathVariable("idRoom")).thenReturn(idRoom);
        Mockito.when(request.bodyToMono(UserInfoByRoomRequest.class)).thenReturn(Mono.just(userInfoByRoomRequest));
        Mockito.when(roomService.leaveRoom(idRoom, userInfoByRoomRequest)).thenReturn(room);
        Mono<ServerResponse> serverResponseLeaveRoom = roomHandler.leaveRoom(request);

//        assertEquals(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(room)).toString(), serverResponseLeaveRoom.toString());
        assertEquals(Objects.requireNonNull(serverResponseLeaveRoom.block()).statusCode(), HttpStatus.OK);
    }

    @Test
    void leaveRoom_Fail_INCORRECT_REQUEST() {
        Mockito.when(request.bodyToMono(UserInfoByRoomRequest.class)).thenReturn(Mono.empty());
        Mono<ServerResponse> serverResponseLeaveRoom = roomHandler.leaveRoom(request);

//        assertEquals(ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(ErrorStatusCodeRoom.INCORRECT_REQUEST)).toString(), serverResponseLeaveRoom.toString());
        assertEquals(Objects.requireNonNull(serverResponseLeaveRoom.block()).statusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void leaveRoom_Fail_ROOM_NOT_EXIT() throws LiveRoomException {
        Mockito.when(request.bodyToMono(UserInfoByRoomRequest.class)).thenReturn(Mono.just(userInfoByRoomRequest));
        Mockito.when(roomService.leaveRoom(null, userInfoByRoomRequest))
                .thenThrow(new LiveRoomException(ErrorStatusCode.ROOM_NOT_EXIT));
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
    void getMessagesByRoom_Pass() throws GetMessagesByRoomException {
        Mockito.when(request.pathVariable("idRoom")).thenReturn(idRoom);
        Mono<ServerResponse> serverResponseGetMessagesByRoom = roomHandler.getMessagesByRoom(request);
        Mockito.when(roomService.getMessagesByRoom(idRoom))
                .thenReturn(Collections.singletonList(messageRoom));

        assertEquals(Objects.requireNonNull(serverResponseGetMessagesByRoom.block()).statusCode(), HttpStatus.OK);
    }

    @Test
    void getMessagesByRoom_Fail() throws GetMessagesByRoomException {
        Mockito.when(roomService.getMessagesByRoom(null))
                .thenThrow(new GetMessagesByRoomException(ErrorStatusCode.ROOM_NOT_EXIT));
        Mono<ServerResponse> serverResponseGetMessagesByRoom = roomHandler.getMessagesByRoom(request);


        assertEquals(Objects.requireNonNull(serverResponseGetMessagesByRoom.block()).statusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void createRoom_Pass() throws CreateRoomException {
        Mockito.when(request.bodyToMono(CreateRoomRequest.class)).thenReturn(Mono.just(createRoomRequest));
        Mockito.when(roomService.createRoom(createRoomRequest.getUserName(), createRoomRequest.getChatName()))
                .thenReturn(room);
        Mono<ServerResponse> serverResponseGetMessagesByRoom = roomHandler.createRoom(request);

        assertEquals(Objects.requireNonNull(serverResponseGetMessagesByRoom.block()).statusCode(), HttpStatus.OK);
    }

    @Test
    void createRoom_Fail_INCORRECT_REQUEST() {
        Mockito.when(request.bodyToMono(CreateRoomRequest.class)).thenReturn(Mono.empty());
        Mono<ServerResponse> serverResponseGetMessagesByRoom = roomHandler.createRoom(request);

        assertEquals(Objects.requireNonNull(serverResponseGetMessagesByRoom.block()).statusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void createRoom_Fail_ROOM_NOT_EXIT() throws CreateRoomException {
        Mockito.when(request.bodyToMono(CreateRoomRequest.class)).thenReturn(Mono.just(createRoomRequest));
        Mockito.when(roomService.createRoom(createRoomRequest.getUserName(), createRoomRequest.getChatName()))
                .thenThrow(new CreateRoomException(ErrorStatusCode.ROOM_NOT_EXIT));
        Mono<ServerResponse> serverResponseGetMessagesByRoom = roomHandler.createRoom(request);

        assertEquals(Objects.requireNonNull(serverResponseGetMessagesByRoom.block()).statusCode(), HttpStatus.BAD_REQUEST);
    }

    private final MessageRoomRequest messageRoomRequest
            = new MessageRoomRequest("UserName","Text");

    @Test
    void saveMessage_Pass() throws SendMessageToRoomException {
        Mockito.when(request.pathVariable("idRoom")).thenReturn(idRoom);
        Mockito.when(request.bodyToMono(MessageRoomRequest.class)).thenReturn(Mono.just(messageRoomRequest));
        Mockito.when(roomService.saveMessage(idRoom, messageRoomRequest))
                .thenReturn(messageRoom);
        Mono<ServerResponse> serverResponseSaveMessage = roomHandler.saveMessage(request);

        assertEquals(Objects.requireNonNull(serverResponseSaveMessage.block()).statusCode(), HttpStatus.OK);
    }

    @Test
    void saveMessage_Fail_ROOM_NOT_EXIT() throws SendMessageToRoomException {
        Mockito.when(request.pathVariable("idRoom")).thenReturn(idRoom);
        Mockito.when(request.bodyToMono(MessageRoomRequest.class)).thenReturn(Mono.just(messageRoomRequest));
        Mockito.when(roomService.saveMessage(idRoom, messageRoomRequest))
                .thenThrow(new SendMessageToRoomException(ErrorStatusCode.ROOM_NOT_EXIT));
        Mono<ServerResponse> serverResponseSaveMessage = roomHandler.saveMessage(request);

        assertEquals(Objects.requireNonNull(serverResponseSaveMessage.block()).statusCode(), HttpStatus.BAD_REQUEST);

    }

    @Test
    void saveMessage_Fail_INCORRECT_REQUEST() {
        Mockito.when(request.bodyToMono(MessageRoomRequest.class)).thenReturn(Mono.empty());
        Mono<ServerResponse> serverResponseSaveMessage = roomHandler.saveMessage(request);

        assertEquals(Objects.requireNonNull(serverResponseSaveMessage.block()).statusCode(), HttpStatus.BAD_REQUEST);
    }

}