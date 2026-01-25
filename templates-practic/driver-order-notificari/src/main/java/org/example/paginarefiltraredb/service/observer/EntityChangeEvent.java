package org.example.paginarefiltraredb.service.observer;

public class EntityChangeEvent<E> {
    private final ChangeEventType type;
    private final E data;
    private final E oldData;

    public EntityChangeEvent(ChangeEventType type, E data) {
        this(type, data, null);
    }

    public EntityChangeEvent(ChangeEventType type, E data, E oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() { return type; }
    public E getData() { return data; }
    public E getOldData() { return oldData; }
}