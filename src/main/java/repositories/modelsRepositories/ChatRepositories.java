package repositories.modelsRepositories;

import models.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepositories extends MongoRepository<Chat, Long> {
    Long findChatIdByChatName(String name);
    String findChatById(Long id);
}
