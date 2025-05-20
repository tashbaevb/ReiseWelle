package de.fhzwickau.reisewelle.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface BaseDao<T> {

    T findById(UUID id) throws SQLException;

    List<T> findAll() throws SQLException;

    void save(T entity) throws SQLException;

    void delete(UUID id) throws SQLException;
}
