package org.zipli.socknet.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.zipli.socknet.repository.model.File;

@Component
public class FileRepository {

    private final MongoTemplate mongoTemplate;

    public FileRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public File getFileById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, File.class);
    }

    public File save(File file) {
        return mongoTemplate.save(file);
    }

    public boolean existsById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        File file = mongoTemplate.findOne(query, File.class);
        return file != null;
    }

    public void deleteById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        mongoTemplate.remove(query, File.class);
    }
}
