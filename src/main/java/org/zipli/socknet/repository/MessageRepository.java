package org.zipli.socknet.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import org.zipli.socknet.repository.model.Message;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Repository
public class MessageRepository {

    private final MongoTemplate mongoTemplate;

    public MessageRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Message getMessageById(String id) {
        Query query = new Query()
                .addCriteria(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, Message.class);
    }

    public void deleteAllByChatId(String chatId) {
        Query query = new Query()
                .addCriteria(Criteria.where("chatId").is(chatId));
        mongoTemplate.remove(query, Message.class);
    }

    public boolean existsByChatId(String chatId) {
        Query query = new Query()
                .addCriteria(Criteria.where("chatId").is(chatId));
        return mongoTemplate.exists(query, Message.class);
    }

    public Message getMessageByIdAndAuthorId(String id, String authorId) {
        Query query = new Query()
                .addCriteria(Criteria.where("id").is(id).and("authorId").is(authorId));
        return mongoTemplate.findOne(query, Message.class);
    }

    public boolean existsById(String id) {
        Query query = new Query()
                .addCriteria(Criteria.where("id").is(id));
        return mongoTemplate.exists(query, Message.class);
    }

    public Message save(Message message) {
        mongoTemplate.save(message);
        return message;
    }

    public void delete(Message message) {
        mongoTemplate.remove(message);
    }
}
