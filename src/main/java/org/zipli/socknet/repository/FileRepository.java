package org.zipli.socknet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.zipli.socknet.model.FileMessage;

import java.util.List;

@Repository
public interface FileRepository extends MongoRepository<FileMessage, String> {
    FileMessage getFileById(String id);

    List<FileMessage> getFilesByAuthorId(String authorId);

    void deleteAllByChatId(String chatId);

    boolean existsByChatId(String chatId);
}
