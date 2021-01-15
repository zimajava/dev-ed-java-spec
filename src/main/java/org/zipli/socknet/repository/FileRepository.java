package org.zipli.socknet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.zipli.socknet.model.File;


@Repository
public interface FileRepository extends MongoRepository<File, String> {
}
