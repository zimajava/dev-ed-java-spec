package org.zipli.socknet.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.zipli.socknet.model.Room;

@Repository
public interface RoomRepository extends MongoRepository<Room, String> {
    boolean existsByChatName(String chatName);
    boolean existsById(String id);
}
