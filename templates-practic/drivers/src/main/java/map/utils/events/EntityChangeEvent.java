package map.utils.events;

import map.domain.Entity;
import map.domain.Order;


public class EntityChangeEvent implements Event {
    private ChangeEventType type;
    private Order data;

    public EntityChangeEvent(ChangeEventType type, Order data) {
        this.type = type;
        this.data = data;
    }


    public ChangeEventType getType() {
        return type;
    }

    public Order getData() {
        return data;
    }

}
