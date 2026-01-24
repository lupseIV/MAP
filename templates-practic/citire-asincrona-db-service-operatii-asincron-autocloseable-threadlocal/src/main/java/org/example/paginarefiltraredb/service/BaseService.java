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

import java.sql.Time;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * Async Service Layer
 *
 * Principles Applied:
 * - Dependency Inversion: Depends on Executor abstraction, not concrete ExecutorService
 * - Single Responsibility: Orchestrates async operations, doesn't manage threads
 * - Open/Closed: Can be extended without modification
 */
public abstract class BaseService<ID, E extends Entity<ID>> extends Observable<E> {

    protected final EntityRepository<ID, E> repository;
    protected final Validator<E> validator;
    protected final Executor executor;

    /**
     * Constructor Injection - Dependency Inversion Principle
     *
     * @param repository Entity repository (remains synchronous)
     * @param validator Entity validator
     * @param executor Executor for async operations (injected dependency)
     */
    public BaseService(EntityRepository<ID, E> repository,
                       Validator<E> validator,
                       Executor executor) {
        this.repository = repository;
        this.validator = validator;
        this.executor = executor;
    }

    // ========================================================================
    // CRUD OPERATIONS - All return CompletableFuture<T>
    // ========================================================================

    /**
     * Add entity asynchronously
     *
     * @param entity Entity to add
     * @return CompletableFuture<E> - null if successfully added, existing entity if duplicate
     */
    public CompletableFuture<E> add(E entity) {
        return CompletableFuture.supplyAsync(() -> {
            // Validation happens on worker thread
            validator.validate(entity);

//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            // Repository call happens on worker thread
            // Repository internally gets its own connection via AutoCloseableConnection
            E saved = repository.save(entity);

            if (saved == null) {
                // Notify observers on worker thread
                notifyObservers(new EntityChangeEvent<>(ChangeEventType.ADD, entity));
                return null;
            }
            return saved;
        }, executor);
    }

    /**
     * Delete entity asynchronously
     *
     * @param id Entity ID to delete
     * @return CompletableFuture<E> - deleted entity if found, null otherwise
     */
    public CompletableFuture<E> delete(ID id) {
        return CompletableFuture.supplyAsync(() -> {
            // Repository call happens on worker thread
            E deleted = repository.delete(id);

            if (deleted != null) {
                notifyObservers(new EntityChangeEvent<>(ChangeEventType.DELETE, deleted));
            }
            return deleted;
        }, executor);
    }

    /**
     * Update entity asynchronously
     *
     * @param entity Entity to update
     * @return CompletableFuture<E> - null if successfully updated, entity if validation failed
     */
    public CompletableFuture<E> update(E entity) {
        return CompletableFuture.supplyAsync(() -> {
            validator.validate(entity);

            // Fetch old entity for observers
            Optional<E> oldEntityOpt = repository.findOne(entity.getId());
            E oldEntity = oldEntityOpt.orElse(null);

            // Repository call happens on worker thread
            E updated = repository.update(entity);

            if (updated == null) {
                notifyObservers(new EntityChangeEvent<>(ChangeEventType.UPDATE, entity, oldEntity));
            }
            return updated;
        }, executor);
    }

    /**
     * Find one entity asynchronously
     *
     * @param id Entity ID
     * @return CompletableFuture<Optional<E>>
     */
    public CompletableFuture<Optional<E>> findOne(ID id) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findOne(id);
        }, executor);
    }

    /**
     * Find all entities asynchronously
     *
     * @return CompletableFuture<Iterable<E>>
     */
    public CompletableFuture<Iterable<E>> findAll() {
        return CompletableFuture.supplyAsync(repository::findAll, executor);
    }

    // ========================================================================
    // PAGINATION & FILTERING - Async
    // ========================================================================

    /**
     * Find all entities on page asynchronously
     *
     * @param pageable Pagination parameters
     * @return CompletableFuture<Page<E>>
     */
    public CompletableFuture<Page<E>> findAllOnPage(Pageable pageable) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findAllOnPage(pageable);
        }, executor);
    }

    /**
     * Find all entities on page with filter asynchronously
     *
     * @param pageable Pagination parameters
     * @param filter SQL filter
     * @return CompletableFuture<Page<E>>
     */
    public CompletableFuture<Page<E>> findAllOnPage(Pageable pageable, SqlFilter filter) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findAllOnPage(pageable, filter);
        }, executor);
    }

    /**
     * Convert page to DTO list asynchronously
     *
     * @param pageFuture Future containing page
     * @param mapper Mapping function
     * @return CompletableFuture<List<DTO>>
     */
    public <DTO> CompletableFuture<List<DTO>> convertPageToDto(
            CompletableFuture<Page<E>> pageFuture,
            Function<E, DTO> mapper) {
        return pageFuture.thenApplyAsync(page -> {
            return DtoUtils.fromPage(page, mapper);
        }, executor);
    }

    /**
     * Convert iterable to DTO list asynchronously
     *
     * @param iterableFuture Future containing iterable
     * @param mapper Mapping function
     * @return CompletableFuture<List<DTO>>
     */
    public <DTO> CompletableFuture<List<DTO>> convertIterableToDto(
            CompletableFuture<Iterable<E>> iterableFuture,
            Function<E, DTO> mapper) {
        return iterableFuture.thenApplyAsync(iterable -> {
            return DtoUtils.fromIterable(iterable, mapper);
        }, executor);
    }

    /**
     * Synchronous helper - Convert page to DTO list
     * Useful when you already have a Page object
     */
    public <DTO> List<DTO> convertToDto(Page<E> page, Function<E, DTO> mapper) {
        return DtoUtils.fromPage(page, mapper);
    }

    /**
     * Synchronous helper - Convert iterable to DTO list
     * Useful when you already have an Iterable object
     */
    public <DTO> List<DTO> convertToDto(Iterable<E> iter, Function<E, DTO> mapper) {
        return DtoUtils.fromIterable(iter, mapper);
    }
}