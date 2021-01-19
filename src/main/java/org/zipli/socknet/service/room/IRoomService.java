package org.zipli.socknet.service.room;

import org.springframework.http.codec.ServerSentEvent;
import org.zipli.socknet.dto.room.BaseSseDto;
import org.zipli.socknet.dto.MessageRoom;
import org.zipli.socknet.dto.RoomsDao;
import org.zipli.socknet.dto.UserInfoByRoom;
import org.zipli.socknet.dto.room.MessageSseDto;
import org.zipli.socknet.exception.message.SendMessageException;
import org.zipli.socknet.exception.room.*;
import org.zipli.socknet.model.Room;
import reactor.core.publisher.Flux;

import java.util.List;

public interface IRoomService {
    Room getRoom(String idRoom) throws GetRoomException;

    List<RoomsDao> getRooms();

    Room joinRoom(String idRoom, UserInfoByRoom userInfoByRoom) throws JoinRoomException;

    Room leaveRoom(String idRoom, UserInfoByRoom userInfoByRoom) throws LiveRoomException;

    void deleteRoom(String idRoom);

    Room createRoom(String userName, String chatName) throws CreateRoomException;

    MessageRoom saveMessage(String idRoom, MessageSseDto message) throws SendMessageException;

    List<MessageRoom> getMessagesByRoom(String idRoom) throws GetMessagesByRoomException;

    Flux<ServerSentEvent<BaseSseDto>> subscribeMessage(String idRoom);

}
