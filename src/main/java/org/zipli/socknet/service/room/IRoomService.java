package org.zipli.socknet.service.room;

import org.springframework.http.codec.ServerSentEvent;
import org.zipli.socknet.dto.RoomMessage;
import org.zipli.socknet.dto.request.MessageRoomRequest;
import org.zipli.socknet.dto.request.UserInfoByRoomRequest;
import org.zipli.socknet.dto.response.RoomResponse;
import org.zipli.socknet.dto.response.RoomsResponse;
import org.zipli.socknet.dto.response.roomEvent.BaseEventResponse;
import org.zipli.socknet.exception.room.*;
import org.zipli.socknet.repository.model.Room;
import reactor.core.publisher.Flux;

import java.util.List;

public interface IRoomService {
    Room getRoom(String idRoom) throws GetRoomException;

    List<Room> getRooms();

    Room joinRoom(String idRoom, UserInfoByRoomRequest userInfoByRoomRequest) throws JoinRoomException;

    Room leaveRoom(String idRoom, UserInfoByRoomRequest userInfoByRoomRequest) throws LiveRoomException;

    void deleteRoom(String idRoom);

    Room createRoom(String userName, String chatName) throws CreateRoomException;

    RoomMessage saveMessage(String idRoom, MessageRoomRequest message) throws SendMessageToRoomException;

    List<RoomMessage> getMessagesByRoom(String idRoom) throws GetMessagesByRoomException;

    Flux<ServerSentEvent<BaseEventResponse>> subscribeMessage(String idRoom);

}
