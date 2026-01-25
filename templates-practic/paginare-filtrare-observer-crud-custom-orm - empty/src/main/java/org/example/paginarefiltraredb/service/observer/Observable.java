package org.example.paginarefiltraredb.service.observer;

import java.util.ArrayList;
import java.util.List;

public class Observable<E> {
    private final List<Observer<E>> observers = new ArrayList<>();

    public void addObserver(Observer<E> observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer<E> observer) {
        observers.remove(observer);
    }

    protected void notifyObservers(EntityChangeEvent<E> event) {
        for (Observer<E> observer : observers) {
            observer.update(event);
        }
    }
}