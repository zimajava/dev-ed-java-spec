package org.zipli.socknet.service.room;

import org.springframework.http.codec.ServerSentEvent;
import org.zipli.socknet.dto.UserInfoByRoom;
import org.zipli.socknet.dto.WsMessageResponse;
import org.zipli.socknet.exception.message.SendMessageException;
import org.zipli.socknet.exception.room.CreateRoomException;
import org.zipli.socknet.exception.room.GetRoomException;
import org.zipli.socknet.exception.room.JoinRoomException;
import org.zipli.socknet.model.Message;
import org.zipli.socknet.model.Room;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IRoomService {
    Mono<Room> getRoom(String idRoom) throws GetRoomException;

    Mono<Room> joinRoom(String idRoom, UserInfoByRoom userInfoByRoom, String signal) throws JoinRoomException;

    Room liveRoom(String idRoom, String userName);

    Mono<Room> createRoom(String idUser, String chatName) throws CreateRoomException;

    Mono<Message> saveMessage(Message message, String idRoom) throws SendMessageException;

    Flux<ServerSentEvent<WsMessageResponse>> getMessage(String idRoom);
}
