package org.domain.dtos.guiDTOS;

import javafx.beans.property.*;

public class EventGuiDTO {

    private final LongProperty id = new SimpleLongProperty();
    private DoubleProperty maxTime = new SimpleDoubleProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty state = new SimpleStringProperty();
    private StringProperty owner = new SimpleStringProperty();
    private LongProperty ducks = new SimpleLongProperty();
    private LongProperty winners = new SimpleLongProperty();


    public EventGuiDTO(Long id, Double maxTime, String name, String state, String owner, Long ducks, Long winners) {
        this.id.set(id);
        if (maxTime != null) this.maxTime.set(maxTime);
        this.name.set(name);
        this.state.set(state);
        this.owner.set(owner);
        this.ducks.set(ducks);
        this.winners.set(winners);
    }
    public LongProperty idProperty() { return id; }
    public DoubleProperty maxTimeProperty() { return maxTime; }
    public StringProperty nameProperty() {
        return name;
    }
    public StringProperty stateProperty() {
        return state;
    }
    public StringProperty ownerProperty() {
        return owner;
    }
    public LongProperty ducksProperty() {
        return ducks;
    }
    public LongProperty winnersProperty() {
        return winners;
    }

    public String getOwner() {
        return owner.get();
    }

    public void setOwner(String owner) {
        this.owner.set(owner);
    }

    public long getDucks() {
        return ducks.get();
    }

    public void setDucks(long ducks) {
        this.ducks.set(ducks);
    }

    public long getWinners() {
        return winners.get();
    }

    public void setWinners(long winners) {
        this.winners.set(winners);
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
