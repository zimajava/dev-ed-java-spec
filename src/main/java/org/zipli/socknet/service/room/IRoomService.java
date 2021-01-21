package org.zipli.socknet.service.room;

import org.springframework.http.codec.ServerSentEvent;
import org.zipli.socknet.dto.MessageRoom;
import org.zipli.socknet.dto.RoomsResponse;
import org.zipli.socknet.dto.UserInfoByRoomResponse;
import org.zipli.socknet.dto.response.BaseEventResponse;
import org.zipli.socknet.dto.response.MessageEventResponse;
import org.zipli.socknet.exception.room.*;
import org.zipli.socknet.model.Room;
import reactor.core.publisher.Flux;

import java.util.List;

public interface IRoomService {
    Room getRoom(String idRoom) throws GetRoomException;

    List<RoomsResponse> getRooms();

    Room joinRoom(String idRoom, UserInfoByRoomResponse userInfoByRoomResponse) throws JoinRoomException;

    Room leaveRoom(String idRoom, UserInfoByRoomResponse userInfoByRoomResponse) throws LiveRoomException;

    void deleteRoom(String idRoom);

    Room createRoom(String userName, String chatName) throws CreateRoomException;

    MessageRoom saveMessage(String idRoom, MessageEventResponse message) throws SendMessageToRoomException;

    List<MessageRoom> getMessagesByRoom(String idRoom) throws GetMessagesByRoomException;

    Flux<ServerSentEvent<BaseEventResponse>> subscribeMessage(String idRoom);

}
