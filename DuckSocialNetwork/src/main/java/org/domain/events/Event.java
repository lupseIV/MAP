package org.domain.events;

import org.domain.Entity;
import org.domain.Observable;
import org.domain.Observer;
import org.utils.enums.EventState;

import java.util.ArrayList;
import java.util.List;

public abstract class Event<E, T extends Observer<E>> extends Entity<Long> implements Observable<E, T> {
    private List<T> subscribers;
    protected EventState state;

    public Event(List<T> subscribers) {
        this.subscribers = (subscribers != null) ? subscribers : new ArrayList<>();
        state = EventState.SCHEDULED;
    }

    public Event() {
        this.subscribers = new ArrayList<>();
        state = EventState.SCHEDULED;
    }

    public void setSubscribers(List<T> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public void addObserver(T o) {
        subscribers.add(o);
    }

    @Override
    public void removeObserver(T o) {
        subscribers.remove(o);
    }

    @Override
    public void notifyObservers(E event) {
        for(T o : subscribers){
            o.update(event);
        }
    }

    public EventState getState() {
        return state;
    }

    public void setState(EventState state) {
        this.state = state;
    }

    public List<T > getSubscribers() {
        return subscribers;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + getId() +
                '}';
    }
}