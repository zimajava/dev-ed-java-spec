package org.zipli.socknet.repositories;

import java.util.List;
import java.util.Optional;

public interface IRepositoryService<T> {
    void save(T t);
    List<T> findAll();
    T update(T t);
    void delete(T t);

    Optional<T> getOneByParams(Object... obj);
    List<T> getByParams(Object... obj);
    List<T> updateByParams(Object... obj);
    List<T> deleteByParams(Object... obj);
}
