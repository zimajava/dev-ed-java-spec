package org.zipli.socknet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.zipli.socknet.model.MessagePrivate;

import java.util.List;

@Repository
public interface MessagePrivateRepository extends MongoRepository<MessagePrivate, String> {
    MessagePrivate getMessageById(String id);
    List<MessagePrivate> getMessagesByAuthorId(String authorId);
}
