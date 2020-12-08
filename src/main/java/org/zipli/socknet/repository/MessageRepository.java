package org.zipli.socknet.repository;

import org.zipli.socknet.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    Message getMessageById(String id);
    List<Message> getMessagesByAuthorId(String authorId);
}
