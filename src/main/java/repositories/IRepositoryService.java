package repositories;

import java.util.List;
import java.util.Optional;

public interface IRepositoryService<T> {
    public void save(T t);
    public List<T> findAll();
    public T update(T t);
    public void delete(T t);

    Optional<T> getOneByParams(Object... obj);
    List<T> getByParams(Object... obj);
    List<T> updateByParams(Object... obj);
    List<T> deleteByParams(Object... obj);
}
