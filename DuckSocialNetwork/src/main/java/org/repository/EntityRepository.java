package org.repository;


import org.domain.Entity;
import org.domain.exceptions.RepositoryException;
import org.domain.validators.Validator;
import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class EntityRepository<ID, E extends Entity<ID>> implements PagingRepository<ID,E> {
    protected ConcurrentHashMap<ID, E> entities;
    protected Validator<E> validator;

    @Override
    public abstract Page<E> findAllOnPage(Pageable pageable);

    public EntityRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new ConcurrentHashMap<>();
    }


    @Override
    public E findOne(ID id) {
        if(id == null)
            throw new RepositoryException("Id can't be null!");
        return entities.get(id);
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public E save(E entity) {
        if(entity == null)
            throw new RepositoryException("Entity can't be null!");
        validator.validate(entity);

        if(findOne(entity.getId()) != null)
            return null;

        return entities.putIfAbsent(entity.getId(), entity);
    }

    @Override
    public E delete(ID id) {
        if(id == null) {
            throw new RepositoryException("Id can't be null!");
        }
        return entities.remove(id);
    }

    @Override
    public E update(E entity) {
        if(entity == null)
            throw new RepositoryException("Entity can't be null!");
        if(findOne(entity.getId()) == null)
            return entity;

        validator.validate(entity);
        entities.replace(entity.getId(), entity);
        return null;
    }

    public static <T extends Comparable<T>> T getMaxId(Iterable<? extends Entity<T>> entities) {

        T maxId = null;

        for (Entity<T> e : entities) {
            if (e.getId() != null) {
                if (maxId == null || e.getId().compareTo(maxId) > 0) {
                    maxId = e.getId();
                }
            }
        }
        return maxId;
    }
}
