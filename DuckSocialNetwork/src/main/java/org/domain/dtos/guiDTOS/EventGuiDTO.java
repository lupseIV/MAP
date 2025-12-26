package org.domain.dtos.guiDTOS;

import javafx.beans.property.*;

public class EventGuiDTO {

    private final LongProperty id = new SimpleLongProperty();
    private DoubleProperty maxTime = new SimpleDoubleProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty state = new SimpleStringProperty();

    public EventGuiDTO(Long id, Double maxTime, String name, String state) {
        this.id.set(id);
        if (maxTime != null) this.maxTime.set(maxTime);
        this.name.set(name);
        this.state.set(state);
    }
    public LongProperty idProperty() { return id; }
    public DoubleProperty maxTimeProperty() { return maxTime; }
    public StringProperty nameProperty() {
        return name;
    }
    public StringProperty stateProperty() {
        return state;
    }

    public String getState() {
        return state.get();
    }

    public void setState(String state) {
        this.state.set(state);
    }

    public long getId() {
        return id.get();
    }
    public double getMaxTime() {
        return maxTime.get();
    }
    public String getName() {
        return name.get();
    }
    public void setMaxTime(double maxTime) {
        this.maxTime.set(maxTime);
    }
    public void setName(String name) {
        this.name.set(name);
    }
}
