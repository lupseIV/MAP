package org.domain;

public interface Observable<E, O extends Observer<E>> {
    void addObserver(O observer);
    void removeObserver(O observer);
    void notifyObservers(E event);
}
