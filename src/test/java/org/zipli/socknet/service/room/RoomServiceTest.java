package org.zipli.socknet.service.room;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.http.codec.ServerSentEvent;
import org.zipli.socknet.dto.MessageRoom;
import org.zipli.socknet.dto.RoomsResponse;
import org.zipli.socknet.dto.UserInfoByRoom;
import org.zipli.socknet.dto.response.BaseEventResponse;
import org.zipli.socknet.dto.response.MessageEventResponse;
import org.zipli.socknet.exception.ErrorStatusCodeRoom;
import org.zipli.socknet.exception.room.*;
import org.zipli.socknet.model.Room;
import org.zipli.socknet.repository.RoomRepository;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataMongoTest
class RoomServiceTest {

    RoomService roomService;

    @Autowired
    RoomRepository roomRepository;

    Room roomOne;

    @BeforeEach
    void setUp() throws CreateRoomException {
        roomService = new RoomService(roomRepository);
        roomOne = roomRepository.save(new Room("RoomOne", "UserOne"));
    }

    @Test
    void getRoom_Pass() throws GetRoomException {
        Room room = roomService.getRoom(roomOne.getId());

        assertEquals(room.getId(), roomOne.getId());
        assertEquals(room.getCreatorUser(), roomOne.getCreatorUser());
        assertEquals(room.getRoomName(), roomOne.getRoomName());
    }

    @Test
    void getRoom_Fail() {
        try {
            roomService.getRoom("NoValidId");
        } catch (GetRoomException e) {
            assertEquals(e.getErrorStatusCodeRoom(),
                    ErrorStatusCodeRoom.ROOM_NOT_EXIT);
        }
    }

    @Test
    void getRooms() {
        List<RoomsResponse> roomsResponse = roomService.getRooms();
        List<Room> rooms = roomRepository.findAll();

        assertEquals(roomsResponse.size(), rooms.size());
        assertEquals(roomsResponse.get(0).getRoomId(), rooms.get(0).getId());
        assertEquals(roomsResponse.get(0).getNameRoom(), rooms.get(0).getRoomName());
    }

    @Test
    void joinRoom_Pass() throws JoinRoomException, CreateRoomException {
        Room roomCreate = roomService.createRoom("User_Join_Pass", "Room_Join_Pass");
        Room roomJoin = roomService.joinRoom(roomCreate.getId(),
                new UserInfoByRoom("Artemiy", "", "signal", false));
        Room room = roomRepository.getRoomById(roomCreate.getId());

        assertEquals(roomJoin.getUsers().get(0).getIdUser(), room.getUsers().get(0).getIdUser());
        assertEquals(roomJoin.getUsers().get(0).getUsername(), room.getUsers().get(0).getUsername());
        assertEquals(roomJoin.getUsers().get(0).getSignals(), room.getUsers().get(0).getSignals());
    }

    @Test
    void joinRoom_Fail() {
        try {
            roomService.joinRoom("NoValidRoom",
                    new UserInfoByRoom("Artemiy", "", "signal", false));
        } catch (JoinRoomException e) {
            assertEquals(e.getErrorStatusCodeRoom(), ErrorStatusCodeRoom.ROOM_NOT_EXIT);
        }
    }

    @Test
    void leaveRoom_Pass() throws CreateRoomException, JoinRoomException, LiveRoomException {
        Room roomCreate = roomService.createRoom("User_Leave_Pass", "Room_Leave_Pass");
        Room roomJoin = roomService.joinRoom(roomCreate.getId(),
                new UserInfoByRoom("Artemiy", "", "signal", false));
        log.info("sdaaaaaaa     " + roomJoin.getUsers().get(0).getUsername());
        Room roomLeave = roomService.leaveRoom(roomCreate.getId(),
                new UserInfoByRoom("Artemiy", "", "signal", false));

        assertNotEquals(roomJoin.getUsers().size(), roomLeave.getUsers().size() - 1);
    }

    @Test
    void createRoom_Pass() throws CreateRoomException {
        Room roomCreate = roomService.createRoom("User_Create_Room", "Room_Create");
        Room room = roomRepository.getRoomById(roomCreate.getId());

        assertEquals(room.getId(), roomCreate.getId());
        assertEquals(room.getCreatorUser(), roomCreate.getCreatorUser());
        assertEquals(room.getRoomName(), roomCreate.getRoomName());
    }

    @Test
    void createRoom_Fail() {
        try {
            roomService.createRoom("User_Create_Room_Fail", "Room_Create_Fail");
            roomService.createRoom("User_Create_Room_Fail", "Room_Create_Fail");
        } catch (CreateRoomException e) {
            assertEquals(e.getErrorStatusCodeRoom(), ErrorStatusCodeRoom.ROOM_ALREADY_EXISTS);
        }
    }

    @Test
    void saveMessage_Pass() throws CreateRoomException, SendMessageToRoomException {
        Room roomCreate = roomService.createRoom("User_SaveMessage_Pass", "Room_SaveMessage_Pass");
        MessageRoom messageRoomSave = roomService.saveMessage(roomCreate.getId(), new MessageEventResponse());
        Room room = roomRepository.getRoomById(roomCreate.getId());

        assertEquals(messageRoomSave.getRoomId(), room.getId());
        assertEquals(messageRoomSave.getTextMessage(), room.getMessages().get(0).getTextMessage());
        assertEquals(messageRoomSave.getAuthorUserName(), room.getMessages().get(0).getAuthorUserName());
    }

    @Test
    void saveMessage_Fail() {
        try {
            roomService.saveMessage("NoValidId", new MessageEventResponse());
        } catch (SendMessageToRoomException e) {
            assertEquals(e.getErrorStatusCodeRoom(), ErrorStatusCodeRoom.INCORRECT_REQUEST);
        }
    }

    @Test
    void getMessagesByRoom_Pass() throws CreateRoomException, GetMessagesByRoomException, SendMessageToRoomException {
        Room roomCreate = roomService.createRoom("User_getMessagesByRoom_Pass", "Room_getMessagesByRoom_Pass");
        roomService.saveMessage(roomCreate.getId(), new MessageEventResponse());
        List<MessageRoom> messagesByRoom = roomService.getMessagesByRoom(roomCreate.getId());
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
            assertEquals(e.getErrorStatusCodeRoom(), ErrorStatusCodeRoom.ROOM_NOT_EXIT);
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