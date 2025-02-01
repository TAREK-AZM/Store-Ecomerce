package org.store.api.repository;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T, ID> {
    List<T> findAll() throws Exception;
    Optional<T> findById(ID id) throws Exception;
    void save(T entity) throws Exception;
    void deleteById(ID id) throws Exception;
}