package org.zipli.socknet.service.room;

import org.springframework.http.codec.ServerSentEvent;
import org.zipli.socknet.dto.MessageDto;
import org.zipli.socknet.dto.MessageRoom;
import org.zipli.socknet.dto.UserInfoByRoom;
import org.zipli.socknet.exception.message.SendMessageException;
import org.zipli.socknet.exception.room.CreateRoomException;
import org.zipli.socknet.exception.room.GetRoomException;
import org.zipli.socknet.exception.room.JoinRoomException;
import org.zipli.socknet.model.Message;
import org.zipli.socknet.model.Room;
import reactor.core.publisher.Flux;

import java.util.List;

public interface IRoomService {
    Room getRoom(String idRoom) throws GetRoomException;

    List<Room> getRooms();

    Room joinRoom(String idRoom, UserInfoByRoom userInfoByRoom) throws JoinRoomException;

    Room liveRoom(String idRoom, String userName);

    Room deleteRoom(String idRoom);

    Room createRoom(String idUser, String chatName) throws CreateRoomException;

    public MessageRoom saveMessage(String idRoom, MessageDto message) throws SendMessageException ;

    Flux<ServerSentEvent<MessageDto>> getMessage(String idRoom);

}
