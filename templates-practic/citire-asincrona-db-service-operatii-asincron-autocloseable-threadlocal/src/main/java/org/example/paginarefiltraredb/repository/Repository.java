package org.example.paginarefiltraredb.repository;

import org.example.paginarefiltraredb.domain.entities.Entity;

import java.util.Optional;

public interface Repository<ID, E extends Entity<ID>> {
    Optional<E> findOne(ID id);
    Iterable<E> findAll();
    E save(E entity);
    E delete(ID id);
    E update(E entity);
}
