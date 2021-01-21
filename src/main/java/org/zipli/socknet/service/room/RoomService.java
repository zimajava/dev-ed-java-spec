package org.zipli.socknet.service.room;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.EventCommandSse;
import org.zipli.socknet.dto.MessageRoom;
import org.zipli.socknet.dto.RoomsResponse;
import org.zipli.socknet.dto.UserInfoByRoomResponse;
import org.zipli.socknet.dto.response.BaseEventResponse;
import org.zipli.socknet.dto.response.MessageEventResponse;
import org.zipli.socknet.dto.response.RoomEventResponse;
import org.zipli.socknet.exception.ErrorStatusCodeRoom;
import org.zipli.socknet.exception.room.*;
import org.zipli.socknet.model.Room;
import org.zipli.socknet.repository.RoomRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class RoomService implements IRoomService {

    private final RoomRepository roomRepository;

    private final Map<String, Sinks.Many<ServerSentEvent<BaseEventResponse>>> chatIdEmitterMap = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> eventIdGeneration = new ConcurrentHashMap<>();
    private final AtomicLong messageIdEvent = new AtomicLong();

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Room getRoom(String idRoom) throws GetRoomException {
        Optional<Room> room = roomRepository.findById(idRoom);

        if (room.isPresent()) {
            return room.get();
        } else {
            throw new GetRoomException(ErrorStatusCodeRoom.ROOM_NOT_EXIT);
        }
    }

    @Override
    public List<RoomsResponse> getRooms() {
        return roomRepository.findAll()
                .stream()
                .map(e -> new RoomsResponse(e.getId(), e.getRoomName()))
                .collect(Collectors.toList());
    }

    @Override
    public Room joinRoom(String idRoom, UserInfoByRoomResponse userInfoByRoomResponse) throws JoinRoomException {
        Optional<Room> roomOptional = roomRepository.findById(idRoom);

        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            room.getUsers().add(userInfoByRoomResponse);
            room = roomRepository.save(room);

            chatIdEmitterMap.get(idRoom).tryEmitNext(ServerSentEvent.<BaseEventResponse>builder()
                    .id(String.valueOf(eventIdGeneration.get(room.getId()).getAndIncrement()))
                    .event(EventCommandSse.JOIN_ROOM_EVENT.name())
                    .data(new RoomEventResponse(
                            idRoom,
                            userInfoByRoomResponse.getSignals(),
                            userInfoByRoomResponse.getUsername()))
                    .build());
            return room;
        } else {
            throw new JoinRoomException(ErrorStatusCodeRoom.ROOM_NOT_EXIT);
        }
    }

    @Override
    public Room leaveRoom(String idRoom, UserInfoByRoomResponse userInfoByRoomResponse) throws LiveRoomException {
        Optional<Room> roomOptional = roomRepository.findById(idRoom);

        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            room.getUsers().removeIf(userInfo -> userInfo.getUsername().equals(userInfoByRoomResponse.getUsername()));
            room = roomRepository.save(room);

            chatIdEmitterMap.get(idRoom).tryEmitNext(ServerSentEvent.<BaseEventResponse>builder()
                    .id(String.valueOf(eventIdGeneration.get(room.getId()).getAndIncrement()))
                    .id("12212")
                    .event(EventCommandSse.LEAVE_ROOM_EVENT.name())
                    .data(new RoomEventResponse(
                            idRoom,
                            userInfoByRoomResponse.getSignals(),
                            userInfoByRoomResponse.getUsername()))
                    .build());
            return room;
        } else {
            throw new LiveRoomException(ErrorStatusCodeRoom.ROOM_NOT_EXIT);
        }
    }

    @Override
    public Room createRoom(String userName, String chatName) throws CreateRoomException {
        if (!roomRepository.existsByRoomName(chatName)) {

            Room room = new Room(chatName, userName);
            room = roomRepository.save(room);
            Sinks.Many<ServerSentEvent<BaseEventResponse>> emitter = Sinks.many().multicast().directAllOrNothing();
            chatIdEmitterMap.put(room.getId(), emitter);
            eventIdGeneration.put(room.getId(),
                    new AtomicLong(System.currentTimeMillis()));
            return room;
        } else {
            throw new CreateRoomException(ErrorStatusCodeRoom.ROOM_ALREADY_EXISTS);
        }
    }

    @Override
    public MessageRoom saveMessage(String idRoom, MessageEventResponse message) throws SendMessageToRoomException {

        Optional<Room> roomOptional = roomRepository.findById(idRoom);

        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            MessageRoom messageRoom = new MessageRoom(message.getUserName(), idRoom, message.getTextMessage(), new Date());
            room.getMessages().add(new MessageRoom(message.getUserName(), idRoom, message.getTextMessage(), new Date()));
            roomRepository.save(room);
            chatIdEmitterMap.get(idRoom).tryEmitNext(ServerSentEvent.<BaseEventResponse>builder()
                    .id(String.valueOf(messageIdEvent.getAndIncrement()))
                    .event(EventCommandSse.NEW_MESSAGE_EVENT.name())
                    .data(message)
                    .build());
            return messageRoom;
        } else {
            throw new SendMessageToRoomException(ErrorStatusCodeRoom.INCORRECT_REQUEST);
        }
    }

    @Override
    public List<MessageRoom> getMessagesByRoom(String idRoom) throws GetMessagesByRoomException {

        Optional<Room> roomOptional = roomRepository.findById(idRoom);
        if (roomOptional.isPresent()) {
            return roomOptional.get().getMessages();
        } else {
            throw new GetMessagesByRoomException(ErrorStatusCodeRoom.ROOM_NOT_EXIT);
        }
    }

    @Override
    public Flux<ServerSentEvent<BaseEventResponse>> subscribeMessage(String idRoom) {
        return chatIdEmitterMap.get(idRoom).asFlux();
    }

    @Override
    public void deleteRoom(String idRoom) {
        roomRepository.deleteById(idRoom);
        chatIdEmitterMap.get(idRoom).tryEmitNext(ServerSentEvent.<BaseEventResponse>builder()
                .id(String.valueOf(messageIdEvent.getAndIncrement()))
                .event(EventCommandSse.DELETE_ROOM_EVENT.name())
                .data(new BaseEventResponse(idRoom))
                .build());
        chatIdEmitterMap.remove(idRoom);
    }
}
