package org.zipli.socknet.service.room;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.EventCommandSse;
import org.zipli.socknet.dto.RoomMessage;
import org.zipli.socknet.dto.request.MessageRoomRequest;
import org.zipli.socknet.dto.response.RoomsResponse;
import org.zipli.socknet.dto.request.UserInfoByRoomRequest;
import org.zipli.socknet.dto.response.BaseEventResponse;
import org.zipli.socknet.dto.response.MessageEventResponse;
import org.zipli.socknet.dto.response.RoomEventResponse;
import org.zipli.socknet.exception.ErrorStatusCode;
import org.zipli.socknet.exception.room.*;
import org.zipli.socknet.repository.model.Room;
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
    private final AtomicLong messageIdEventGenerator = new AtomicLong();

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Room getRoom(String idRoom) throws GetRoomException {
        Optional<Room> room = roomRepository.findById(idRoom);

        if (room.isPresent()) {
            return room.get();
        } else {
            throw new GetRoomException(ErrorStatusCode.ROOM_NOT_EXIT);
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
    public Room joinRoom(String idRoom, UserInfoByRoomRequest userInfoByRoomRequest) throws JoinRoomException {
        Optional<Room> roomOptional = roomRepository.findById(idRoom);

        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            room.getUsers().add(userInfoByRoomRequest);
            room = roomRepository.save(room);

            chatIdEmitterMap.get(idRoom).tryEmitNext(ServerSentEvent.<BaseEventResponse>builder()
                    .id(String.valueOf(eventIdGeneration.get(room.getId()).getAndIncrement()))
                    .event(EventCommandSse.JOIN_ROOM_EVENT.name())
                    .data(new RoomEventResponse(
                            idRoom,
                            userInfoByRoomRequest.getSignals(),
                            userInfoByRoomRequest.getUsername()))
                    .build());
            return room;
        } else {
            throw new JoinRoomException(ErrorStatusCode.ROOM_NOT_EXIT);
        }
    }

    @Override
    public Room leaveRoom(String idRoom, UserInfoByRoomRequest userInfoByRoomRequest) throws LiveRoomException {
        Optional<Room> roomOptional = roomRepository.findById(idRoom);

        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            room.getUsers().removeIf(userInfo -> userInfo.getUsername().equals(userInfoByRoomRequest.getUsername()));
            room = roomRepository.save(room);

            chatIdEmitterMap.get(idRoom).tryEmitNext(ServerSentEvent.<BaseEventResponse>builder()
                    .id(String.valueOf(eventIdGeneration.get(room.getId()).getAndIncrement()))
                    .id("12212")
                    .event(EventCommandSse.LEAVE_ROOM_EVENT.name())
                    .data(new RoomEventResponse(
                            idRoom,
                            userInfoByRoomRequest.getSignals(),
                            userInfoByRoomRequest.getUsername()))
                    .build());
            return room;
        } else {
            throw new LiveRoomException(ErrorStatusCode.ROOM_NOT_EXIT);
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
            throw new CreateRoomException(ErrorStatusCode.ROOM_ALREADY_EXISTS);
        }
    }

    @Override
    public RoomMessage saveMessage(String idRoom, MessageRoomRequest message) throws SendMessageToRoomException {

        Optional<Room> roomOptional = roomRepository.findById(idRoom);

        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            RoomMessage roomMessage = new RoomMessage(message.getUserName(), idRoom, message.getTextMessage(), new Date());
            room.getMessages().add(new RoomMessage(message.getUserName(), idRoom, message.getTextMessage(), new Date()));
            roomRepository.save(room);
            chatIdEmitterMap.get(idRoom).tryEmitNext(ServerSentEvent.<BaseEventResponse>builder()
                    .id(String.valueOf(messageIdEventGenerator.getAndIncrement()))
                    .event(EventCommandSse.NEW_MESSAGE_EVENT.name())
                    .data(new MessageEventResponse())
                    .build());
            return roomMessage;
        } else {
            throw new SendMessageToRoomException(ErrorStatusCode.INCORRECT_REQUEST);
        }
    }

    @Override
    public List<RoomMessage> getMessagesByRoom(String idRoom) throws GetMessagesByRoomException {

        Optional<Room> roomOptional = roomRepository.findById(idRoom);
        if (roomOptional.isPresent()) {
            return roomOptional.get().getMessages();
        } else {
            throw new GetMessagesByRoomException(ErrorStatusCode.ROOM_NOT_EXIT);
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
                .id(String.valueOf(messageIdEventGenerator.getAndIncrement()))
                .event(EventCommandSse.DELETE_ROOM_EVENT.name())
                .data(new BaseEventResponse(idRoom))
                .build());
        chatIdEmitterMap.remove(idRoom);
    }
}
