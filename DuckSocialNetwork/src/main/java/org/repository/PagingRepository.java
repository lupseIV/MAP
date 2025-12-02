package org.repository;

import org.domain.Entity;
import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;

public interface PagingRepository<ID , E extends Entity<ID>> extends Repository<ID, E> {

    Page<E> findAllOnPage(Pageable pageable);
}