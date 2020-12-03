package org.zipli.socknet.repositories.modelsRepositories;

import org.zipli.socknet.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {
    User getUserByEmail(String email);
    User getByUserName(String userName);
    User findByUserName(String userName);
    User findByEmail(String email);
    List<User> getUsersByNickName(String nickName);
    List<User> getUserByConfirm(boolean isConfirm);
    void deleteByEmail(String email);
    List<User> deleteAllByUserName(String userName);
    List<User> deleteByNickName(String nickName);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);

}
