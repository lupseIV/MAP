package org.example.paginarefiltraredb.service;

import org.example.paginarefiltraredb.domain.dtos.DtoUtils;
import org.example.paginarefiltraredb.domain.entities.Entity;
import org.example.paginarefiltraredb.domain.filters.SqlFilter;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.paging.EntityRepository;
import org.example.paginarefiltraredb.repository.paging.util.paging.Page;
import org.example.paginarefiltraredb.repository.paging.util.paging.Pageable;
import org.example.paginarefiltraredb.service.observer.ChangeEventType;
import org.example.paginarefiltraredb.service.observer.EntityChangeEvent;
import org.example.paginarefiltraredb.service.observer.Observable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public abstract class BaseService<ID, E extends Entity<ID>> extends Observable<E> {

    protected final EntityRepository<ID, E> repository;
    protected final Validator<E> validator;
    private Executor executor;

    public BaseService(EntityRepository<ID, E> repository, Validator<E> validator, Executor executors) {
        this.repository = repository;
        this.validator = validator;
        this.executor = executors;
    }

    // --- CRUD OPERATIONS ---

    public E add(E entity) {
        validator.validate(entity);
        E saved = repository.save(entity);

        if (saved == null) {
            notifyObservers(new EntityChangeEvent<>(ChangeEventType.ADD, entity));
            return null;
        }
        return saved;
    }

    public E delete(ID id) {
        E deleted = repository.delete(id);
        if (deleted != null) {
            notifyObservers(new EntityChangeEvent<>(ChangeEventType.DELETE, deleted));
        }
        return deleted;
    }

    public E update(E entity) {
        validator.validate(entity);

        Optional<E> oldEntityOpt = repository.findOne(entity.getId());
        E oldEntity = oldEntityOpt.orElse(null);

        E updated = repository.update(entity);

        if (updated == null) {
            notifyObservers(new EntityChangeEvent<>(ChangeEventType.UPDATE, entity, oldEntity));
        }
        return updated;
    }

    public Optional<E> findOne(ID id) {
        return repository.findOne(id);
    }


    public CompletableFuture<Iterable<E>> findAll() {
        // Runs on worker thread
        return CompletableFuture.supplyAsync(repository::findAll, executor);
    }
    public CompletableFuture<Iterable<E>>findAll(SqlFilter filter) {return CompletableFuture.supplyAsync(() -> {
        return repository.findAll(filter);
    }, executor);}
     // --- PAGINATION & FILTERING ---

    public Page<E> findAllOnPage(Pageable pageable) {
        return repository.findAllOnPage(pageable);
    }

    public Page<E> findAllOnPage(Pageable pageable, SqlFilter filter) {
        return repository.findAllOnPage(pageable, filter);
    }

    public <DTO> List<DTO> convertToDto(Page<E> page, Function<E, DTO> mapper) {
        return DtoUtils.fromPage(page, mapper);
    }

    public <DTO> List<DTO> convertToDto(Iterable<E> iter, Function<E, DTO> mapper) {
        return DtoUtils.fromIterable(iter, mapper);
    }
}