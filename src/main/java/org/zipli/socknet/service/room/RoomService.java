package org.zipli.socknet.service.room;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.EventCommandRoom;
import org.zipli.socknet.dto.RoomMessage;
import org.zipli.socknet.dto.request.MessageRoomRequest;
import org.zipli.socknet.dto.request.UserInfoByRoomRequest;
import org.zipli.socknet.dto.response.RoomResponse;
import org.zipli.socknet.dto.response.RoomsResponse;
import org.zipli.socknet.dto.response.roomEvent.BaseEventResponse;
import org.zipli.socknet.dto.response.roomEvent.MessageEventResponse;
import org.zipli.socknet.dto.response.roomEvent.RoomEventResponse;
import org.zipli.socknet.exception.ErrorStatusCode;
import org.zipli.socknet.exception.room.*;
import org.zipli.socknet.repository.RoomRepository;
import org.zipli.socknet.repository.model.Room;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoomService implements IRoomService {

    private final RoomRepository roomRepository;

    private final Map<String, Sinks.Many<ServerSentEvent<BaseEventResponse>>> messageEmitterByRoomId = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> eventIdGeneration = new ConcurrentHashMap<>();

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Room getRoom(String roomId) throws GetRoomException {
        Optional<Room> roomOptional = roomRepository.findById(roomId);

        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            log.info("Get Room successful: RoomId - {}, RoomName - {}, number of users - {}",
                    room.getId(),
                    room.getRoomName(),
                    room.getUsersInfo().size()
            );
            return room;
        } else {
            throw new GetRoomException(ErrorStatusCode.ROOM_NOT_EXIT);
        }
    }

    @Override
    public List<Room> getRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Room joinRoom(String roomId, UserInfoByRoomRequest userInfoByRoomRequest) throws JoinRoomException {
        Optional<Room> roomOptional = roomRepository.findById(roomId);

        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            room.getUsersInfo().add(userInfoByRoomRequest);
            room = roomRepository.save(room);

            messageEmitterByRoomId.get(roomId).tryEmitNext(ServerSentEvent.<BaseEventResponse>builder()
                    .id(String.valueOf(eventIdGeneration.get(room.getId()).getAndIncrement()))
                    .event(EventCommandRoom.JOIN_ROOM_EVENT.name())
                    .data(new RoomEventResponse(userInfoByRoomRequest.getUserName(),
                            userInfoByRoomRequest.getSignal()))
                    .build());
            log.info("Join Room successful: Room - {}, user - {}, number of users - {}",
                    room.getId(),
                    userInfoByRoomRequest.getUserName(),
                    room.getUsersInfo().size()
            );
            return room;
        } else {
            throw new JoinRoomException(ErrorStatusCode.ROOM_NOT_EXIT);
        }
    }

    @Override
    public Room leaveRoom(String roomId, UserInfoByRoomRequest userInfoByRoomRequest) throws LiveRoomException {
        Optional<Room> roomOptional = roomRepository.findById(roomId);

        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            if (room.getUsersInfo() != null && userInfoByRoomRequest.getUserName() != null) {
                room.getUsersInfo().removeIf(userInfo -> userInfo.getUserName().equals(userInfoByRoomRequest.getUserName()));
                room = roomRepository.save(room);

                messageEmitterByRoomId.get(roomId).tryEmitNext(ServerSentEvent.<BaseEventResponse>builder()
                        .id(String.valueOf(eventIdGeneration.get(room.getId()).getAndIncrement()))
                        .event(EventCommandRoom.LEAVE_ROOM_EVENT.name())
                        .data(new RoomEventResponse(userInfoByRoomRequest.getUserName(),
                                userInfoByRoomRequest.getSignal()))
                        .build());

                log.info("Leave Room successful: Room - {}, user - {}, number of users - {}",
                        room.getId(),
                        userInfoByRoomRequest.getUserName(),
                        room.getUsersInfo().size()
                );
            } else {
                throw new LiveRoomException(ErrorStatusCode.INCORRECT_REQUEST);
            }
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
            messageEmitterByRoomId.put(room.getId(), emitter);
            eventIdGeneration.put(room.getId(),
                    new AtomicLong(System.currentTimeMillis()));
            log.info("Create Room successful: RoomId - {}, RoomName - {}, users - {}",
                    room.getId(),
                    room.getRoomName(),
                    room.getCreatorUserName()
            );
            return room;
        } else {
            throw new CreateRoomException(ErrorStatusCode.ROOM_ALREADY_EXISTS);
        }
    }

    @Override
    public RoomMessage saveMessage(String roomId, MessageRoomRequest message) throws SendMessageToRoomException {

        Optional<Room> roomOptional = roomRepository.findById(roomId);

        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            RoomMessage roomMessage = new RoomMessage(message.getUserName(), message.getTextMessage(), new Date().getTime());
            room.getMessages().add(roomMessage);
            roomRepository.save(room);
            messageEmitterByRoomId.get(roomId).tryEmitNext(ServerSentEvent.<BaseEventResponse>builder()
                    .id(String.valueOf(eventIdGeneration.get(room.getId()).getAndIncrement()))
                    .event(EventCommandRoom.NEW_MESSAGE_EVENT.name())
                    .data(new MessageEventResponse(
                            roomMessage.getAuthorUserName(),
                            roomMessage.getTextMessage()))
                    .build());

            return roomMessage;
        } else {
            throw new SendMessageToRoomException(ErrorStatusCode.INCORRECT_REQUEST);
        }
    }

    @Override
    public List<RoomMessage> getMessagesByRoom(String roomId) throws GetMessagesByRoomException {

        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            log.info("Get Messages by Room successful: RoomId - {}, amount of messages - {}",
                    room.getId(),
                    room.getMessages().size()
            );
            return room.getMessages();
        } else {
            throw new GetMessagesByRoomException(ErrorStatusCode.ROOM_NOT_EXIT);
        }
    }

    @Override
    public Flux<ServerSentEvent<BaseEventResponse>> subscribeMessage(String idRoom) {
        return messageEmitterByRoomId.get(idRoom).asFlux();
    }

    @Override
    public void deleteRoom(String roomId) {
        roomRepository.deleteById(roomId);
        messageEmitterByRoomId.get(roomId).tryEmitNext(ServerSentEvent.<BaseEventResponse>builder()
                .id(String.valueOf(eventIdGeneration.get(roomId).getAndIncrement()))
                .event(EventCommandRoom.DELETE_ROOM_EVENT.name())
                .build());
        messageEmitterByRoomId.remove(roomId);
        log.info("Delete Room successful: RoomId - {}", roomId);
    }
}
