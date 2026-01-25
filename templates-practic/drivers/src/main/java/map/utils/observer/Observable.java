package map.utils.observer;

import map.utils.events.Event;

public interface Observable<E  extends Event> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E event);
}
