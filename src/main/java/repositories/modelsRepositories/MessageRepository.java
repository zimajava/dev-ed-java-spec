package repositories.modelsRepositories;

import models.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, Long> {
    List<Message> getMessagesByChatId(Long id);
    List<Message> getMessagesByAuthorId(Long userId);
    String getMessageByMessageId(Long id);
}
