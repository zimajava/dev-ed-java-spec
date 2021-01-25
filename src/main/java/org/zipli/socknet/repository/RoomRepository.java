package org.zipli.socknet.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.zipli.socknet.repository.model.Room;

@Repository
public interface RoomRepository extends MongoRepository<Room, String> {
    boolean existsByRoomName(String chatName);

    boolean existsById(String id);

    Room getRoomById(String id);
}
