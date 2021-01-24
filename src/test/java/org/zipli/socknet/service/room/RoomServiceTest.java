package org.zipli.socknet.service.room;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.http.codec.ServerSentEvent;
import org.zipli.socknet.dto.RoomMessage;
import org.zipli.socknet.dto.request.MessageRoomRequest;
import org.zipli.socknet.dto.response.RoomResponse;
import org.zipli.socknet.dto.response.RoomsResponse;
import org.zipli.socknet.dto.request.UserInfoByRoomRequest;
import org.zipli.socknet.dto.response.roomEvent.BaseEventResponse;
import org.zipli.socknet.exception.ErrorStatusCode;
import org.zipli.socknet.exception.room.*;
import org.zipli.socknet.repository.model.Room;
import org.zipli.socknet.repository.RoomRepository;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataMongoTest
class RoomServiceTest {

    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    private Room roomOne;
    private Room roomTwo;

    @BeforeEach
    void setUp() throws CreateRoomException {
        roomService = new RoomService(roomRepository);
        roomOne = roomRepository.save(new Room("RoomOne", "UserOne"));
        roomTwo = roomRepository.save(new Room("RoomTwo", "UserTwo"));
    }

    @Test
    void getRoom_Pass() throws GetRoomException {
        Room room = roomService.getRoom(roomOne.getId());

        assertEquals(room.getId(), roomOne.getId());
        assertEquals(room.getCreatorUserName(), roomOne.getCreatorUserName());
        assertEquals(room.getRoomName(), roomOne.getRoomName());
    }

    @Test
    void getRoom_Fail() {
        try {
            roomService.getRoom("NoValidId");
        } catch (GetRoomException e) {
            assertEquals(e.getErrorStatusCodeRoom(),
                    ErrorStatusCode.ROOM_NOT_EXIT);
        }
    }

    @Test
    void getRooms() {
        List<RoomsResponse> roomsResponse = roomService.getRooms();
        List<Room> rooms = roomRepository.findAll();

        assertEquals(roomsResponse.size(), rooms.size());
        assertEquals(roomsResponse.get(0).getRoomId(), rooms.get(0).getId());
        assertEquals(roomsResponse.get(0).getNameRoom(), rooms.get(0).getRoomName());
        assertEquals(roomsResponse.get(1).getRoomId(), rooms.get(1).getId());
        assertEquals(roomsResponse.get(1).getNameRoom(), rooms.get(1).getRoomName());
    }

    @Test
    void joinRoom_Pass() throws JoinRoomException, CreateRoomException {
        Room roomCreate = roomService.createRoom("User_Join_Pass", "Room_Join_Pass");
        RoomResponse roomJoin = roomService.joinRoom(roomCreate.getId(),
                new UserInfoByRoomRequest("Artemiy", "", "signal"));
        Room room = roomRepository.getRoomById(roomCreate.getId());

        assertEquals(roomJoin.getUsers().get(0).getUserId(), room.getUsersInfo().get(0).getUserId());
        assertEquals(roomJoin.getUsers().get(0).getUserName(), room.getUsersInfo().get(0).getUserName());
    }

    @Test
    void joinRoom_Fail() {
        try {
            roomService.joinRoom("NoValidRoom",
                    new UserInfoByRoomRequest("Artemiy", "", "signal"));
        } catch (JoinRoomException e) {
            assertEquals(e.getErrorStatusCodeRoom(), ErrorStatusCode.ROOM_NOT_EXIT);
        }
    }

    @Test
    void leaveRoom_Pass() throws CreateRoomException, JoinRoomException, LiveRoomException {
        Room roomCreate = roomService.createRoom("User_Leave_Pass", "Room_Leave_Pass");
        RoomResponse roomJoin = roomService.joinRoom(roomCreate.getId(),
                new UserInfoByRoomRequest("Artemiy", "", "signal"));
        log.info("sdaaaaaaa     " + roomJoin.getUsers().get(0).getUserName());
        RoomResponse roomLeave = roomService.leaveRoom(roomCreate.getId(),
                new UserInfoByRoomRequest("Artemiy", "", "signal"));

        assertEquals(roomJoin.getUsers().size()-1, roomLeave.getUsers().size());
    }

    @Test
    void createRoom_Pass() throws CreateRoomException {
        Room roomCreate = roomService.createRoom("User_Create_Room", "Room_Create");
        Room room = roomRepository.getRoomById(roomCreate.getId());

        assertEquals(room.getId(), roomCreate.getId());
        assertEquals(room.getCreatorUserName(), roomCreate.getCreatorUserName());
        assertEquals(room.getRoomName(), roomCreate.getRoomName());
    }

    @Test
    void createRoom_Fail() {
        try {
            roomService.createRoom("User_Create_Room_Fail", "Room_Create_Fail");
            roomService.createRoom("User_Create_Room_Fail", "Room_Create_Fail");
        } catch (CreateRoomException e) {
            assertEquals(e.getErrorStatusCodeRoom(), ErrorStatusCode.ROOM_ALREADY_EXISTS);
        }
    }

    @Test
    void saveMessage_Pass() throws CreateRoomException, SendMessageToRoomException {
        Room roomCreate = roomService.createRoom("User_SaveMessage_Pass", "Room_SaveMessage_Pass");
        RoomMessage roomMessageSave = roomService.saveMessage(roomCreate.getId(), new MessageRoomRequest());
        Room room = roomRepository.getRoomById(roomCreate.getId());

        assertEquals(roomMessageSave.getRoomId(), room.getId());
        assertEquals(roomMessageSave.getTextMessage(), room.getMessages().get(0).getTextMessage());
        assertEquals(roomMessageSave.getAuthorUserName(), room.getMessages().get(0).getAuthorUserName());
    }

    @Test
    void saveMessage_Fail() {
        try {
            roomService.saveMessage("NoValidId", new MessageRoomRequest());
        } catch (SendMessageToRoomException e) {
            assertEquals(e.getErrorStatusCodeRoom(), ErrorStatusCode.INCORRECT_REQUEST);
        }
    }

    @Test
    void getMessagesByRoom_Pass() throws CreateRoomException, GetMessagesByRoomException, SendMessageToRoomException {
        Room roomCreate = roomService.createRoom("User_getMessagesByRoom_Pass", "Room_getMessagesByRoom_Pass");
        roomService.saveMessage(roomCreate.getId(), new MessageRoomRequest());
        List<RoomMessage> messagesByRoom = roomService.getMessagesByRoom(roomCreate.getId());
        Room room = roomRepository.getRoomById(roomCreate.getId());

        assertEquals(messagesByRoom.get(0).getRoomId(), room.getId());
        assertEquals(messagesByRoom.get(0).getTextMessage(), room.getMessages().get(0).getTextMessage());
        assertEquals(messagesByRoom.get(0).getAuthorUserName(), room.getMessages().get(0).getAuthorUserName());
    }

    @Test
    void getMessagesByRoom_Fail() {
        try {
            roomService.getMessagesByRoom("NoValidId");
        } catch (GetMessagesByRoomException e) {
            assertEquals(e.getErrorStatusCodeRoom(), ErrorStatusCode.ROOM_NOT_EXIT);
        }
    }

    @Test
    void subscribeMessage() throws CreateRoomException {
        Room roomCreate = roomService.createRoom("User_subscribeMessage_Pass", "Room_subscribeMessage_Pass");
        Flux<ServerSentEvent<BaseEventResponse>> serverSentEventFlux = roomService.subscribeMessage(roomCreate.getId());
        assertNotNull(serverSentEventFlux);
    }

    @Test
    void deleteRoom_Pass() throws CreateRoomException {
        Room roomCreate = roomService.createRoom("User_deleteRoom_Pass", "Room_deleteRoom_Pass");
        roomService.deleteRoom(roomCreate.getId());
        assertFalse(roomRepository.existsByRoomName(roomCreate.getRoomName()));
    }
}