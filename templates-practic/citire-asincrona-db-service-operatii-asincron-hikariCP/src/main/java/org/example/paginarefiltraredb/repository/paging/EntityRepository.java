package org.example.paginarefiltraredb.repository.paging;


import org.example.paginarefiltraredb.domain.entities.Entity;
import org.example.paginarefiltraredb.domain.exceptions.RepositoryException;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.paging.util.paging.Page;
import org.example.paginarefiltraredb.repository.paging.util.paging.Pageable;

import java.util.Optional;
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
    public Optional<E> findOne(ID id) {
        if(id == null)
            throw new RepositoryException("Id can't be null!");
        return Optional.ofNullable(entities.get(id));
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

        if (entity.getId() == null) {
            ID newId = generateNewId();
            entity.setId(newId);
            entities.put(newId, entity);
            return null;
        }

        if (entities.containsKey(entity.getId())) {
            return entities.get(entity.getId());
        }

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
        if(findOne(entity.getId()).isEmpty())
            return entity;

        validator.validate(entity);
        entities.replace(entity.getId(), entity);
        return null;
    }

    protected abstract ID generateNewId();
}