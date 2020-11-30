package repositories.modelsRepositories;

import models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {
    List<User> getByEmail(String email);
    List<User> getByUserName(String userName);
    List<User> deleteByEmail(String email);
    List<User> deleteByUserName(String userName);
    boolean existsByUserName(String userName);
}
