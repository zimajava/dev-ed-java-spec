package org.zipli.socknet.reactive;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.zipli.socknet.dto.RoomMessage;
import org.zipli.socknet.dto.request.CreateRoomRequest;
import org.zipli.socknet.dto.request.MessageRoomRequest;
import org.zipli.socknet.dto.request.UserInfoByRoomRequest;
import org.zipli.socknet.dto.response.DeleteRoomResponse;
import org.zipli.socknet.dto.response.ErrorResponse;
import org.zipli.socknet.dto.response.RoomResponse;
import org.zipli.socknet.dto.response.RoomsResponse;
import org.zipli.socknet.dto.response.roomEvent.MessageEventResponse;
import org.zipli.socknet.exception.ErrorStatusCode;
import org.zipli.socknet.exception.room.*;
import org.zipli.socknet.repository.model.Room;
import org.zipli.socknet.service.room.RoomService;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoomHandlerTest {

    private final Room room = new Room("RoomName", "CreatorUser");
    private final RoomResponse roomResponse = new RoomResponse(room.getId(), room.getRoomName(), room.getCreatorUserName(), room.getUsersInfo());
    private final String idRoom = "1";
    private final UserInfoByRoomRequest userInfoByRoomRequest =
            new UserInfoByRoomRequest("UserName", "UserId", "Signal");
    private final CreateRoomRequest createRoomRequest = new CreateRoomRequest("UserName", "ChatName");
    private final RoomMessage roomMessage =
            new RoomMessage("AuthorUser", "IdRoom", "Text", new Date().getTime());
    private final MessageEventResponse messageEventResponse =
            new MessageEventResponse();
    private final MessageRoomRequest messageRoomRequest
            = new MessageRoomRequest("UserName", "Text");
    @Autowired
    private RoomHandler roomHandler;
    @MockBean
    private RoomService roomService;
    @MockBean
    private ServerRequest request;
    private List<RoomsResponse> roomsResponses;
    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        roomsResponses = new ArrayList<>();
        roomsResponses.add(new RoomsResponse("123", "RoomName"));
    }

    @Test
    void getRoom_Pass() throws GetRoomException {
        Mockito.when(request.pathVariable("roomId")).thenReturn(idRoom);
        Mockito.when(roomService.getRoom(idRoom)).thenReturn(room);
        Mono<ServerResponse> serverResponseGetRoom = roomHandler.getRoom(request);

        Room roomBody = webTestClient.get().uri("/zipli/room/getRoom/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Room.class).returnResult().getResponseBody();

        assertEquals(roomBody.getCreatorUserName(), room.getCreatorUserName());
        assertEquals(roomBody.getRoomName(), room.getRoomName());
        assertEquals(Objects.requireNonNull(serverResponseGetRoom.block()).statusCode(), HttpStatus.OK);
    }

    @Test
    void getRoom_Fail() throws GetRoomException {
        Mockito.when(request.pathVariable("roomId")).thenReturn(idRoom);
        Mockito.when(roomService.getRoom(idRoom)).thenThrow(new GetRoomException(ErrorStatusCode.ROOM_NOT_EXIT));
        Mono<ServerResponse> serverResponseGetRoom = roomHandler.getRoom(request);

        ErrorResponse errorResponse = webTestClient.get().uri("/zipli/room/getRoom/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertEquals(errorResponse.getCode(), ErrorStatusCode.ROOM_NOT_EXIT.getValue());
        assertEquals(errorResponse.getReason(), ErrorStatusCode.ROOM_NOT_EXIT.getMessage());
        assertEquals(Objects.requireNonNull(serverResponseGetRoom.block()).statusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void getRooms() {
        Mockito.when(roomService.getRooms()).thenReturn(roomsResponses);
        Mono<ServerResponse> serverResponseGetRooms = roomHandler.getRooms(request);

        List<RoomsResponse> responseBody = webTestClient.get().uri("/zipli/room/getRooms")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<RoomsResponse>>() {
                })
                .returnResult()
                .getResponseBody();

        assertEquals(responseBody.get(0).getRoomId(), roomsResponses.get(0).getRoomId());
        assertEquals(Objects.requireNonNull(serverResponseGetRooms.block()).statusCode(), HttpStatus.OK);
    }

    @Test
    void joinRoom_Pass() throws JoinRoomException {
        Mockito.when(request.pathVariable("roomId")).thenReturn(idRoom);
        Mockito.when(request.bodyToMono(UserInfoByRoomRequest.class)).thenReturn(Mono.just(userInfoByRoomRequest));
        Mockito.when(roomService.joinRoom(idRoom, userInfoByRoomRequest)).thenReturn(roomResponse);
        Mono<ServerResponse> serverResponseJoinRoom = roomHandler.joinRoom(request);

        assertEquals(Objects.requireNonNull(serverResponseJoinRoom.block()).statusCode(), HttpStatus.OK);
    }

    @Test
    void joinRoom_Fail_INCORRECT_REQUEST() {
        Mockito.when(request.pathVariable("roomId")).thenReturn(idRoom);
        Mockito.when(request.bodyToMono(UserInfoByRoomRequest.class)).thenReturn(Mono.empty());
        Mono<ServerResponse> serverResponseJoinRoom = roomHandler.joinRoom(request);

        ErrorResponse errorResponse = webTestClient.post().uri("/zipli/room/joinRoom/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertEquals(errorResponse.getCode(), ErrorStatusCode.INCORRECT_REQUEST.getValue());
        assertEquals(errorResponse.getReason(), ErrorStatusCode.INCORRECT_REQUEST.getMessage());
        assertEquals(Objects.requireNonNull(serverResponseJoinRoom.block()).statusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void joinRoom_Fail_ROOM_NOT_EXIT() throws JoinRoomException {
        Mockito.when(request.bodyToMono(UserInfoByRoomRequest.class)).thenReturn(Mono.just(userInfoByRoomRequest));
        Mockito.when(request.pathVariable("roomId")).thenReturn(idRoom);
        Mockito.when(roomService.joinRoom(idRoom, userInfoByRoomRequest))
                .thenThrow(new JoinRoomException(ErrorStatusCode.ROOM_NOT_EXIT));
        Mono<ServerResponse> serverResponseJoinRoom = roomHandler.joinRoom(request);

        assertEquals(Objects.requireNonNull(serverResponseJoinRoom.block()).statusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void leaveRoom_Pass() throws LiveRoomException {
        Mockito.when(request.pathVariable("roomId")).thenReturn(idRoom);
        Mockito.when(request.bodyToMono(UserInfoByRoomRequest.class)).thenReturn(Mono.just(userInfoByRoomRequest));
        Mockito.when(roomService.leaveRoom(idRoom, userInfoByRoomRequest)).thenReturn(roomResponse);
        Mono<ServerResponse> serverResponseLeaveRoom = roomHandler.leaveRoom(request);

        assertEquals(Objects.requireNonNull(serverResponseLeaveRoom.block()).statusCode(), HttpStatus.OK);
    }

    @Test
    void leaveRoom_Fail_INCORRECT_REQUEST() {
        Mockito.when(request.bodyToMono(UserInfoByRoomRequest.class)).thenReturn(Mono.empty());
        Mono<ServerResponse> serverResponseLeaveRoom = roomHandler.leaveRoom(request);

        ErrorResponse errorResponse = webTestClient.post().uri("/zipli/room/leaveRoom/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertEquals(errorResponse.getCode(), ErrorStatusCode.INCORRECT_REQUEST.getValue());
        assertEquals(errorResponse.getReason(), ErrorStatusCode.INCORRECT_REQUEST.getMessage());
        assertEquals(Objects.requireNonNull(serverResponseLeaveRoom.block()).statusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void leaveRoom_Fail_ROOM_NOT_EXIT() throws LiveRoomException {
        Mockito.when(request.bodyToMono(UserInfoByRoomRequest.class)).thenReturn(Mono.just(userInfoByRoomRequest));
        Mockito.when(roomService.leaveRoom(null, userInfoByRoomRequest))
                .thenThrow(new LiveRoomException(ErrorStatusCode.ROOM_NOT_EXIT));
        Mono<ServerResponse> serverResponseLeaveRoom = roomHandler.leaveRoom(request);

        assertEquals(Objects.requireNonNull(serverResponseLeaveRoom.block()).statusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void deleteRoom_Pass() {
        Mockito.when(request.pathVariable("roomId")).thenReturn(idRoom);
        Mono<ServerResponse> serverResponseDeleteRoom = roomHandler.deleteRoom(request);

        DeleteRoomResponse responseBody = webTestClient.post().uri("/zipli/room/deleteRoom/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DeleteRoomResponse.class)
                .returnResult()
                .getResponseBody();

        assertEquals(responseBody.getReport(), "Ok");
        assertEquals(Objects.requireNonNull(serverResponseDeleteRoom.block()).statusCode(), HttpStatus.OK);
    }

    @Test
    void getMessagesByRoom_Pass() throws GetMessagesByRoomException {
        Mockito.when(request.pathVariable("roomId")).thenReturn(idRoom);
        Mono<ServerResponse> serverResponseGetMessagesByRoom = roomHandler.getMessagesByRoom(request);
        Mockito.when(roomService.getMessagesByRoom(idRoom))
                .thenReturn(Collections.singletonList(roomMessage));

        List<RoomMessage> responseBody = webTestClient.get().uri("/zipli/room/getMessages/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<RoomMessage>>() {
                })
                .returnResult()
                .getResponseBody();

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

        ErrorResponse errorResponse = webTestClient.post().uri("/zipli/room/createRoom")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertEquals(errorResponse.getCode(), ErrorStatusCode.INCORRECT_REQUEST.getValue());
        assertEquals(errorResponse.getReason(), ErrorStatusCode.INCORRECT_REQUEST.getMessage());
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

    @Test
    void saveMessage_Pass() throws SendMessageToRoomException {
        Mockito.when(request.pathVariable("roomId")).thenReturn(idRoom);
        Mockito.when(request.bodyToMono(MessageRoomRequest.class)).thenReturn(Mono.just(messageRoomRequest));
        Mockito.when(roomService.saveMessage(idRoom, messageRoomRequest))
                .thenReturn(roomMessage);
        Mono<ServerResponse> serverResponseSaveMessage = roomHandler.saveMessage(request);

        assertEquals(Objects.requireNonNull(serverResponseSaveMessage.block()).statusCode(), HttpStatus.OK);
    }

    @Test
    void saveMessage_Fail_ROOM_NOT_EXIT() throws SendMessageToRoomException {
        Mockito.when(request.pathVariable("roomId")).thenReturn(idRoom);
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

        ErrorResponse errorResponse = webTestClient.post().uri("/zipli/room/createRoom")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertEquals(errorResponse.getCode(), ErrorStatusCode.INCORRECT_REQUEST.getValue());
        assertEquals(errorResponse.getReason(), ErrorStatusCode.INCORRECT_REQUEST.getMessage());
        assertEquals(Objects.requireNonNull(serverResponseSaveMessage.block()).statusCode(), HttpStatus.BAD_REQUEST);
    }

}