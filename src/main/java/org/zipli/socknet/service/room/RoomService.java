package org.zipli.socknet.service.room;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.UserInfoByRoom;
import org.zipli.socknet.dto.WsMessageResponse;
import org.zipli.socknet.exception.message.SendMessageException;
import org.zipli.socknet.exception.room.CreateRoomException;
import org.zipli.socknet.exception.room.GetRoomException;
import org.zipli.socknet.exception.room.JoinRoomException;
import org.zipli.socknet.model.Message;
import org.zipli.socknet.model.Room;
import org.zipli.socknet.repository.MessageRepository;
import org.zipli.socknet.repository.RoomRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService implements IRoomService {

    final RoomService roomService;
    final RoomRepository roomRepository;
    final MessageRepository messageRepository;

    private final Map<String, Sinks.Many<ServerSentEvent<WsMessageResponse>>> emitterMap = new ConcurrentHashMap<>();

    public RoomService(RoomService roomService, RoomRepository roomRepository, MessageRepository messageRepository) {
        this.roomService = roomService;
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Mono<Room> getRoom(String idRoom) throws GetRoomException {
        Optional<Room> room = roomRepository.findById(idRoom);

        if (room.isPresent()) {
            return Mono.just(room.get());
        } else {
            throw new GetRoomException("Room {} not exit");
        }
    }

    @Override
    public Mono<Room> joinRoom(String idRoom, UserInfoByRoom userInfoByRoom, String signal) throws JoinRoomException {
        Optional<Room> roomOptional = roomRepository.findById(idRoom);

        if (roomOptional.isPresent()) {

            Room room = roomOptional.get();

            room.getUsers().add(userInfoByRoom);
            room.getSignals().add(signal);
            roomRepository.save(room);
            return Mono.just(room);
        } else {
            throw new JoinRoomException("Room {} not exit");
        }
    }

    @Override
    public Room liveRoom(String idRoom, String userName) {
        return null;
    }

    @Override
    public Mono<Room> createRoom(String idUser, String chatName) throws CreateRoomException {
        if (!roomRepository.existsByChatName(chatName)) {
            Room room = new Room(chatName, idUser);
            room = roomRepository.save(room);
            Sinks.Many<ServerSentEvent<WsMessageResponse>> emitter = Sinks.many().multicast().directAllOrNothing();
            emitterMap.put(room.getId(), emitter);
            return Mono.just(room);
        } else {
            throw new CreateRoomException("Such a room {} already exists");
        }
    }

    @Override
    public Mono<Message> saveMessage(Message message, String idRoom) throws SendMessageException {

        if (roomRepository.existsById(idRoom)) {
            message = messageRepository.save(message);
            emitterMap.get(message.getChatId()).tryEmitNext(ServerSentEvent.<WsMessageResponse>builder()
                    .id(String.valueOf(message.getId()))
                    .event("periodic-event")
                    .data(new WsMessageResponse())
                    .build());
            return Mono.just(message);
        } else {
            throw new SendMessageException("Room {} not exit");
        }
    }

    @Override
    public Flux<ServerSentEvent<WsMessageResponse>> getMessage(String idRoom) {
        return emitterMap.get(idRoom).asFlux();
    }
}
