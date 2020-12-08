package org.zipli.socknet.repository;

import org.zipli.socknet.model.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
    Chat findChatById(String id);
    List<Chat> getChatsByPrivate(boolean isPrivate);
}
