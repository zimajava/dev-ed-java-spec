package org.zipli.socknet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.zipli.socknet.model.User;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User getUserByEmail(String email);

    User getUserByUserName(String userName);

    User getUserById(String id);

    List<User> findUsersByIdIn(Collection<String> id);

    List<User> findAllByIsConfirm(boolean confirm);
    User findUserByEmail(String email);

    User findUserByUserName(String username);
}
