package org.zipli.socknet.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.zipli.socknet.repository.model.Chat;

import java.util.Collection;
import java.util.List;

@Component
public class ChatRepository {

    private final MongoTemplate mongoTemplate;
@Component
public class ChatRepository {

    private final MongoTemplate mongoTemplate;

    public ChatRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Chat save(Chat chat) {
        return mongoTemplate.save(chat);
    }

    public void deleteAll() {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").exists(true));
        mongoTemplate.remove(query, Chat.class);
    }

    public ChatRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    public Chat findChatById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, Chat.class);
    }

    public Chat save(Chat chat) {
        return mongoTemplate.save(chat);
    }
    public boolean existsByChatName(String chatName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("chatName").is(chatName));
        return mongoTemplate.exists(query, Chat.class);
    }

    public void deleteAll() {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").exists(true));
        mongoTemplate.remove(query, Chat.class);
    }

    public Chat findChatById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, Chat.class);
    }

    public boolean existsByChatName(String chatName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("chatName").is(chatName));
        return mongoTemplate.exists(query, Chat.class);
    }

    public void deleteById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        mongoTemplate.remove(query, Chat.class);
    }

    public List<Chat> getChatsByIdIn(Collection<String> id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(id));
        return mongoTemplate.find(query, Chat.class);
    }
    public void deleteById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        mongoTemplate.remove(query, Chat.class);
    }

    public List<Chat> getChatsByIdIn(Collection<String> id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(id));
        return mongoTemplate.find(query, Chat.class);
    }

    public void update(String chatId, String fileId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(chatId));
        Update update = new Update();
        update.addToSet("filesId", fileId);
        mongoTemplate.updateFirst(query, update, Chat.class);
    }
}
