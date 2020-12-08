package org.zipli.socknet.repositories.modelsRepositories;

import org.zipli.socknet.models.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepositories extends MongoRepository<Chat, String> {
    Chat findChatById(String id);
    List<Chat> getChatsByPrivate(boolean isPrivate);
}
