package org.zipli.socknet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.zipli.socknet.model.User;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User getUserByEmail(String email);

    User getByUserName(String userName);

    User getUserById(String id);

    User findUserByEmailAndPassword(String email, String password);

    User findUserByUserNameAndPassword(String userName, String password);

    List<User> getUsersByNickName(String nickName);

    List<User> getAllUsersByConfirm(boolean isConfirm);

    void deleteByEmail(String email);

    List<User> deleteByNickName(String nickName);

    boolean deleteByUserName(String userName);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);

    boolean existsByEmailAndPassword(String email, String password);
}
