package org.zipli.socknet.repository;

import org.zipli.socknet.model.MessageGlobal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageGlobalRepository extends MongoRepository<MessageGlobal, String> {
    MessageGlobal getMessageById(String id);
    List<MessageGlobal> getMessagesByAuthorId(String authorId);
}
