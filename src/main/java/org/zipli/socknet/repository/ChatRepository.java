package org.zipli.socknet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.zipli.socknet.model.Chat;

import java.util.Collection;
import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
    Chat findChatById(String id);

    List<Chat> getChatsByPrivate(boolean isPrivate);

    boolean existsByChatName(String chatName);

    boolean existsByChatNameAndCreatorUserId(String chatName, String creatorUserId);

    Chat getByChatNameAndCreatorUserId(String chatName, String creatorUserId);

    void deleteById(String id);

    List<Chat> getChatsByIdIn(Collection<String> id);

    Chat getByIdAndCreatorUserId(String id, String creatorUserId);
}
