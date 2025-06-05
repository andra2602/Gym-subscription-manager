package dao;

import java.util.Optional;

public abstract class BaseDAO<T, K> {
    public abstract void create(T obj);
    public abstract Optional<T> read(K id);
}
