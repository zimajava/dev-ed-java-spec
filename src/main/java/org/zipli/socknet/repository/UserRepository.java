package org.zipli.socknet.repository;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.zipli.socknet.repository.model.User;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Update.update;

import org.springframework.data.mongodb.core.query.Update;

import java.util.Collection;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
public class UserRepository {

    private final MongoTemplate mongoTemplate;

    public UserRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
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
                .addCriteria(where("email").is(email));
        return mongoTemplate.findOne(query, User.class);
    }

    public User getUserByUserName(String userName) {
        Query query = new Query()
                .addCriteria(where("userName").is(userName));
        return mongoTemplate.findOne(query, User.class);
    }

    public User getUserById(String id) {
        Query query = new Query()
                .addCriteria(where("id").is(id));
        return mongoTemplate.findOne(query, User.class);
    }

    public List<User> findUsersByIdIn(Collection<String> id) {
        Query query = new Query()
                .addCriteria(where("id").in(id));
        return mongoTemplate.find(query, User.class);
    }

    public List<User> findAllByIsConfirm(boolean confirm) {
        Query query = new Query()
                .addCriteria(where("isConfirm").is(confirm));
        return mongoTemplate.find(query, User.class);
    }

    public User findUserByEmail(String email) {
        Query query = new Query()
                .addCriteria(where("email").is(email));
        return mongoTemplate.findOne(query, User.class);
    }

    public void deleteById(String id) {
        Query query = new Query()
                .addCriteria(Criteria.where("id").is(id));
        mongoTemplate.remove(query, User.class);
    }

    public User updateOrDeleteAvatar(String userId, String avatar) {
        Query query = new Query()
                .addCriteria(where("id").is(userId));
        Update update = new Update()
                .set("avatar", avatar);
        return mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), User.class);
    }

    public User updateNickName(String userId, String nickName) {
        Query query = new Query()
                .addCriteria(where("id").is(userId));
        Update update = new Update()
                .set("nickName", nickName);
        return mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), User.class);
    }

    public User updateEmail(String userId, String email) {
        Query query = new Query()
                .addCriteria(where("id").is(userId));
        Update update = new Update()
                .set("email", email)
                .set("isConfirm", false);
        return mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), User.class);
    }

    public User updatePassword(String userId, String password) {
        Query query = new Query()
                .addCriteria(where("id").is(userId));
        Update update = new Update()
                .set("password", password);
        return mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), User.class);
    }

    public List<User> findUsersBySearchParam(String param) {
        Criteria criteria = new Criteria();
        Query query = new Query(criteria);

        criteria.orOperator(
                Criteria.where("userName").regex("^" + param),
                Criteria.where("nickName").regex("^" + param),
                Criteria.where("email").regex("^" + param));

        return mongoTemplate.find(query, User.class);
    }


    public void confirmAccountInUsersModel(String userName) {
        Query query = new Query();
        query.addCriteria(where("userName").is(userName));
        Update update = new Update();
        update.set("isConfirm", true);
        mongoTemplate.updateFirst(query, update, User.class);
    }

    public void updatePasswordInUsersModel(String userName, String codedPassword) {
        Query query = new Query();
        query.addCriteria(where("userName").is(userName));
        Update update = new Update();
        update.set("password", codedPassword);
        mongoTemplate.updateFirst(query, update, User.class);
    }
}
