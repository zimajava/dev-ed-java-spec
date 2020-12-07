package org.zipli.socknet.repositories.modelsRepositories;

import org.zipli.socknet.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {
    User getUserByEmail(String email);
    User getByUserName(String userName);
    List<User> getUsersByNickName(String nickName);
    List<User> getAllUserByConfirm(boolean isConfirm);
    void deleteByEmail(String email);
    List<User> deleteByNickName(String nickName);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    boolean deleteByUserName(String userName);
}
