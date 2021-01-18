package org.zipli.socknet.service.room;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.zipli.socknet.dto.MessageDto;
import org.zipli.socknet.dto.MessageRoom;
import org.zipli.socknet.dto.UserInfoByRoom;
import org.zipli.socknet.exception.message.SendMessageException;
import org.zipli.socknet.exception.room.CreateRoomException;
import org.zipli.socknet.exception.room.GetRoomException;
import org.zipli.socknet.exception.room.JoinRoomException;
import org.zipli.socknet.model.Room;
import org.zipli.socknet.repository.MessageRepository;
import org.zipli.socknet.repository.RoomRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RoomService implements IRoomService {

    final RoomRepository roomRepository;
    final MessageRepository messageRepository;

    public RoomService(RoomRepository roomRepository, MessageRepository messageRepository) {
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
    }

    private final Map<String, Sinks.Many<ServerSentEvent<MessageDto>>> emitterMap = new ConcurrentHashMap<>();//messageDTO
    private final Map<String, AtomicLong> eventIdGeneration = new ConcurrentHashMap<>();
    private final AtomicLong messageIdEvent = new AtomicLong();

    @Override
    public Room getRoom(String idRoom) throws GetRoomException {
        Optional<Room> room = roomRepository.findById(idRoom);

        if (room.isPresent()) {
            return room.get();
        } else {
            throw new GetRoomException("Room {} not exit");
        }
    }

    @Override
    public List<Room> getRooms() {
        return null;
    }

    @Override
    public Room joinRoom(String idRoom, UserInfoByRoom userInfoByRoom) throws JoinRoomException {
        Optional<Room> roomOptional = roomRepository.findById(idRoom);

        if (roomOptional.isPresent()) {

            Room room = roomOptional.get();

            room.getUsers().add(userInfoByRoom);
            room = roomRepository.save(room);

            emitterMap.get(idRoom).tryEmitNext(ServerSentEvent.<MessageDto>builder()
                    .id(String.valueOf(eventIdGeneration.get(room.getId()).getAndIncrement()))
                    .event("new_message_event")//переимновать
                    .data(new MessageDto())//в руме ид, юзернейм кто присойдинился, его сигнал
                    .build());

            return room;
        } else {
            throw new JoinRoomException("Room {} not exit");
        }
    }

    @Override
    public Room liveRoom(String idRoom, String userName) {
        return null;
    }

    @Override
    public Room createRoom(String idUser, String chatName) throws CreateRoomException {
        if (!roomRepository.existsByChatName(chatName)) {
            Room room = new Room(chatName, idUser);
            room = roomRepository.save(room);
            Sinks.Many<ServerSentEvent<MessageDto>> emitter = Sinks.many().multicast().directAllOrNothing();
            emitterMap.put(room.getId(), emitter);
            eventIdGeneration.put(room.getId(), new AtomicLong(System.currentTimeMillis()));
            return room;
        } else {
            throw new CreateRoomException("Such a room {} already exists");
        }
    }

    @Override
    public MessageRoom saveMessage(String idRoom, MessageDto message) throws SendMessageException {//изменить на дто

        Optional<Room> roomOptional = roomRepository.findById(idRoom);

        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            MessageRoom messageRoom = new MessageRoom(message.getUserName(), idRoom, message.getTextMessage(), new Date());
            room.getMessages().add(new MessageRoom(message.getUserName(),idRoom,message.getTextMessage(),new Date()));
            emitterMap.get(idRoom).tryEmitNext(ServerSentEvent.<MessageDto>builder()
                    .id(String.valueOf(messageIdEvent.getAndIncrement()))
                    .event("new_message_event")
                    .data(message)
                    .build());
            return messageRoom;
        } else {
            throw new SendMessageException("Room {} not exit");
        }
    }

    @Override
    public Flux<ServerSentEvent<MessageDto>> getMessage(String idRoom) {
        return emitterMap.get(idRoom).asFlux();
    }

    @Override
    public Room deleteRoom(String idRoom) {
        return null;
    }
}
