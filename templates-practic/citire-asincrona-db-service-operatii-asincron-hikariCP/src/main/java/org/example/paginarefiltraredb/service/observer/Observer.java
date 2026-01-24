package org.example.paginarefiltraredb.service.observer;

public interface Observer<E> {
    void update(EntityChangeEvent<E> event);
}
