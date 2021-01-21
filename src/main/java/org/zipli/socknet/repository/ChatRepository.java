package org.zipli.socknet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.zipli.socknet.repository.model.Chat;

import java.util.Collection;
import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
    Chat findChatById(String id);

    boolean existsByChatName(String chatName);

    void deleteById(String id);

    List<Chat> getChatsByIdIn(Collection<String> id);
}
