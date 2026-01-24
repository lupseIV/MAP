package org.example.paginarefiltraredb.repository.paging;

import org.example.paginarefiltraredb.domain.entities.Entity;
import org.example.paginarefiltraredb.domain.filters.SqlFilter;
import org.example.paginarefiltraredb.repository.Repository;
import org.example.paginarefiltraredb.repository.paging.util.paging.Page;
import org.example.paginarefiltraredb.repository.paging.util.paging.Pageable;

public interface PagingRepository<ID , E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> findAllOnPage(Pageable pageable, SqlFilter sqlFilter);
    Page<E> findAllOnPage(Pageable pageable);
}