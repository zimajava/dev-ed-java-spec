package org.zipli.socknet.service.room;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.*;
import org.zipli.socknet.dto.MessageRoom;
import org.zipli.socknet.dto.room.BaseSseDto;
import org.zipli.socknet.dto.room.MessageSseDto;
import org.zipli.socknet.dto.room.RoomSseDto;
import org.zipli.socknet.exception.ErrorStatusCodeRoom;
import org.zipli.socknet.exception.message.SendMessageException;
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

    private final Map<String, Sinks.Many<ServerSentEvent<BaseSseDto>>> chatIdEmitterMap = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> eventIdGeneration = new ConcurrentHashMap<>();
    private final AtomicLong messageIdEvent = new AtomicLong();
    private final String ROOM_NOT_EXIT = "Room {} not exit";

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Room getRoom(String idRoom) throws GetRoomException {
        Optional<Room> room = roomRepository.findById(idRoom);

        if (room.isPresent()) {
            return room.get();
        } else {
            throw new GetRoomException(ROOM_NOT_EXIT, ErrorStatusCodeRoom.ROOM_NOT_EXIT);
        }
    }

    @Override
    public List<RoomsDao> getRooms() {
        return roomRepository.findAll()
                .stream()
                .map(e -> new RoomsDao(e.getId(), e.getChatName()))
                .collect(Collectors.toList());
    }

    @Override
    public Room joinRoom(String idRoom, UserInfoByRoom userInfoByRoom) throws JoinRoomException {
        Optional<Room> roomOptional = roomRepository.findById(idRoom);

        if (roomOptional.isPresent()) {

            Room room = roomOptional.get();
            room.getUsers().add(userInfoByRoom);
            room = roomRepository.save(room);

            chatIdEmitterMap.get(idRoom).tryEmitNext(ServerSentEvent.<BaseSseDto>builder()
                    .id(String.valueOf(eventIdGeneration.get(room.getId()).getAndIncrement()))
                    .event(EventCommandSse.JOIN_ROOM_EVENT.name())
                    .data(new RoomSseDto(
                            idRoom,
                            userInfoByRoom.getSignals(),
                            userInfoByRoom.getUsername()))
                    .build());
            return room;
        } else {
            throw new JoinRoomException(ROOM_NOT_EXIT,ErrorStatusCodeRoom.ROOM_NOT_EXIT);
        }
    }

    @Override
    public Room leaveRoom(String idRoom, UserInfoByRoom userInfoByRoom) throws LiveRoomException {
        Optional<Room> roomOptional = roomRepository.findById(idRoom);

        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            room.getUsers().stream().filter(e -> !e.getUsername().equals(userInfoByRoom.getUsername()));//проверить
            room = roomRepository.save(room);

            chatIdEmitterMap.get(idRoom).tryEmitNext(ServerSentEvent.<BaseSseDto>builder()
                    .id(String.valueOf(eventIdGeneration.get(room.getId()).getAndIncrement()))
                    .event(EventCommandSse.LEAVE_ROOM_EVENT.name())
                    .data(new RoomSseDto(
                            idRoom,
                            userInfoByRoom.getSignals(),
                            userInfoByRoom.getUsername()))
                    .build());
            return room;
        } else {
            throw new LiveRoomException(ROOM_NOT_EXIT,ErrorStatusCodeRoom.ROOM_NOT_EXIT);
        }
    }

    @Override
    public Room createRoom(String userName, String chatName) throws CreateRoomException {
        if (!roomRepository.existsByChatName(chatName)) {

            Room room = new Room(chatName, userName);
            room = roomRepository.save(room);
            Sinks.Many<ServerSentEvent<BaseSseDto>> emitter = Sinks.many().multicast().directAllOrNothing();
            chatIdEmitterMap.put(room.getId(), emitter);
            eventIdGeneration.put(room.getId(),
                    new AtomicLong(System.currentTimeMillis()));
            return room;
        } else {
            throw new CreateRoomException(ROOM_NOT_EXIT,ErrorStatusCodeRoom.ROOM_NOT_EXIT);
        }
    }

    @Override
    public MessageRoom saveMessage(String idRoom, MessageSseDto message) throws SendMessageException {

        Optional<Room> roomOptional = roomRepository.findById(idRoom);

        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            MessageRoom messageRoom = new MessageRoom(message.getUserName(), idRoom, message.getTextMessage(), new Date());
            room.getMessages().add(new MessageRoom(message.getUserName(), idRoom, message.getTextMessage(), new Date()));
            chatIdEmitterMap.get(idRoom).tryEmitNext(ServerSentEvent.<BaseSseDto>builder()
                    .id(String.valueOf(messageIdEvent.getAndIncrement()))
                    .event(EventCommandSse.NEW_MESSAGE_EVENT.name())
                    .data(message)
                    .build());
            return messageRoom;
        } else {
            throw new SendMessageException(ROOM_NOT_EXIT);
        }
    }

    @Override
    public List<MessageRoom> getMessagesByRoom(String idRoom) throws GetMessagesByRoomException {

        Optional<Room> roomOptional = roomRepository.findById(idRoom);
        if (roomOptional.isPresent()) {
            return roomOptional.get().getMessages();
        } else {
            throw new GetMessagesByRoomException(ROOM_NOT_EXIT,ErrorStatusCodeRoom.ROOM_NOT_EXIT);
        }
    }

    @Override
    public Flux<ServerSentEvent<BaseSseDto>> subscribeMessage(String idRoom) {
        return chatIdEmitterMap.get(idRoom).asFlux();
    }

    @Override
    public void deleteRoom(String idRoom) {
        roomRepository.deleteById(idRoom);
        chatIdEmitterMap.remove(idRoom);
        chatIdEmitterMap.get(idRoom).tryEmitNext(ServerSentEvent.<BaseSseDto>builder()
                .id(String.valueOf(messageIdEvent.getAndIncrement()))
                .event(EventCommandSse.DELETE_ROOM_EVENT.name())
                .data(new BaseSseDto(idRoom))
                .build());

    }
}
