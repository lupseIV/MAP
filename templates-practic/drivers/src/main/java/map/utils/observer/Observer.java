package map.utils.observer;

import map.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E event);
}
