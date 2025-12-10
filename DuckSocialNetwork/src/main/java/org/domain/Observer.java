package org.domain;

public interface Observer<T> {
    void update(T event);
}
