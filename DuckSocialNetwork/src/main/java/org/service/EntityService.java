package org.service;

import org.domain.Entity;
import org.domain.validators.Validator;
import org.repository.PagingRepository;
import org.repository.Repository;
import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;
import org.service.utils.IdGenerator;

public abstract class EntityService<ID, E extends Entity<ID>> implements Service<ID, E> {
    protected Validator<E> validator;
    protected PagingRepository<ID, E> repository;
    protected IdGenerator<ID> idGenerator;

    public EntityService(Validator<E> validator, PagingRepository<ID, E> repository, IdGenerator<ID> idGenerator) {
        this.validator = validator;
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    @Override
    public E findOne(ID id) {
        return repository.findOne(id);
    }

    @Override
    public Iterable<E> findAll() {
        return repository.findAll();
    }

    public Page<E> findAllOnPage(Pageable pageable) { return repository.findAllOnPage(pageable);}

    @Override
    public E save(E entity) {
        validator.validate(entity);
        entity.setId(idGenerator.nextId());
        return repository.save(entity);
    }

    @Override
    public E delete(ID id) {
        return repository.delete(id);
    }

    @Override
    public E update(E entity) {
        return repository.update(entity);
    }


}
