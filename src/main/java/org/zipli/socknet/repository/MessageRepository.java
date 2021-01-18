package org.zipli.socknet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.zipli.socknet.model.Message;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    Message getMessageById(String id);

    void deleteAllByChatId(String chatId);

    boolean existsByChatId(String chatId);

    Message getMessageByIdAndAuthorId(String id, String authorId);
}
