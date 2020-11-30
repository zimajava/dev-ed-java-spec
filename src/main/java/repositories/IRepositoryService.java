package repositories;

import java.util.List;
import java.util.Optional;

public interface IRepositoryService<T> {
    public void save(T t);
    public List<T> findAll();
    public T update(T t);
    public void delete(T t);

    public Optional<T> getOneByParams(Object... obj);
    public List<T> getByParams(Object... obj);
    public List<T> updateByParams(Object... obj);
    public List<T> deleteByParams(Object... obj);
}
