package repositories.modelsRepositories;

import models.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, Long> {
    List<Message> getMessagesByInputChat(String inputChat);
    List<Message> getMessagesByOutputChat(String outputChat);
    Message getMessageByInputChat(String inputChat);
    Message getMessageByOutputChat(String outputChat);
    Long getMessageById(Long id);
}
