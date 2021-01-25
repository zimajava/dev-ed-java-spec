package org.zipli.socknet.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.zipli.socknet.repository.model.User;

import java.util.Collection;
import java.util.List;

@Component
public class UserRepository {

    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;

    public UserRepository(MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder) {
        this.mongoTemplate = mongoTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public User save(User user) {
        return mongoTemplate.save(user);
    }

    public void saveAll(List<User> users) {
        for (User user : users) {
            mongoTemplate.save(user);
        }
    }

    public User getUserByEmail(String email) {
        Query query = new Query()
                .addCriteria(Criteria.where("email").is(email));
        return mongoTemplate.findOne(query, User.class);
    }

    public User getUserByUserName(String userName) {
        Query query = new Query()
                .addCriteria(Criteria.where("userName").is(userName));
        return mongoTemplate.findOne(query, User.class);
    }

    public User getUserById(String id) {
        Query query = new Query()
                .addCriteria(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, User.class);
    }

    public List<User> findUsersByIdIn(Collection<String> id) {
        Query query = new Query()
                .addCriteria(Criteria.where("id").in(id));
        return mongoTemplate.find(query, User.class);
    }

    public List<User> findAllByIsConfirm(boolean confirm) {
        Query query = new Query()
                .addCriteria(Criteria.where("isConfirm").is(confirm));
        return mongoTemplate.find(query, User.class);
    }

    public User findUserByEmail(String email) {
        Query query = new Query()
                .addCriteria(Criteria.where("email").is(email));
        return mongoTemplate.findOne(query, User.class);
    }

    public User findUserByUserName(String username) {
        Query query = new Query()
                .addCriteria(Criteria.where("userName").is(username));
        return mongoTemplate.findOne(query, User.class);
    }

    public void deleteById(String id) {
        Query query = new Query()
                .addCriteria(Criteria.where("id").is(id));
        mongoTemplate.remove(query, User.class);
    }

    public void confirmAccountInUsersModel(String userName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(userName));
        Update update = new Update();
        update.set("isConfirm", true);
        mongoTemplate.updateFirst(query, update, User.class);
    }

    public void updatePasswordInUsersModel(String userName, String newPassword) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(userName));
        Update update = new Update();
        update.set("password", passwordEncoder.encode(newPassword));
        mongoTemplate.updateFirst(query, update, User.class);
    }
}