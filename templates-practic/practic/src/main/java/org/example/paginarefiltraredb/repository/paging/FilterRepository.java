package org.example.paginarefiltraredb.repository.paging;

import org.example.paginarefiltraredb.domain.entities.Entity;
import org.example.paginarefiltraredb.domain.filters.SqlFilter;
import org.example.paginarefiltraredb.repository.Repository;

public interface FilterRepository<ID,E extends Entity<ID>> extends Repository<ID,E> {
    Iterable<E> findAll(SqlFilter filter);
}
