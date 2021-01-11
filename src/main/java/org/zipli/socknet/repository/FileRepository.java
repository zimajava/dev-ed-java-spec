package org.zipli.socknet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.zipli.socknet.model.File;

import java.util.List;

@Repository
public interface FileRepository extends MongoRepository<File, String> {
    File getFileById(String id);

    List<File> getFilesByAuthorId(String authorId);

    void deleteAllByChatId(String chatId);

    boolean existsByChatId(String chatId);
}
