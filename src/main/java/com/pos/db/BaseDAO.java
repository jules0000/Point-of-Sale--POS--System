package com.pos.db;

import java.util.List;
import java.util.Optional;

public interface BaseDAO<T> {
    T save(T entity) throws Exception;
    Optional<T> findById(String id) throws Exception;
    List<T> findAll() throws Exception;
    void update(T entity) throws Exception;
    void delete(String id) throws Exception;
    void deleteAll() throws Exception;
} 