package org.domain.events;

import org.domain.Observer;
import org.domain.users.duck.SwimmingDuck;
import org.domain.users.person.Person;

import java.util.ArrayList;
import java.util.List;

public class RaceEvent extends Event<RaceEvent,SwimmingDuck> {

    private Double maxTime = 0.0;
    private List<Integer> distances = new ArrayList<>();
    private String name;
    private Person owner;

    public RaceEvent(List<SwimmingDuck> subscribers, String name, Person owner) {
        super(new ArrayList<>());
        this.name = name;
        this.owner = owner;

        if (subscribers != null) {
            for (SwimmingDuck subscriber : subscribers) {
                this.addObserver(subscriber);
            }
        }
    }

    public RaceEvent() {
        super();
    }

    public RaceEvent(String name, Person owner) {
        super(new ArrayList<>());
        this.name = name;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public Double getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Double maxTime) {
        this.maxTime = maxTime;
    }

    public List<Integer> getDistances() {
        return distances;
    }

    public void setDistances(List<Integer> distances) {
        this.distances = distances;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + " RaceEvent{" +
                "maxTime=" + maxTime +
                ", name='" + name + '\'' +
                ", distances=" + distances +
                '}';
    }
}