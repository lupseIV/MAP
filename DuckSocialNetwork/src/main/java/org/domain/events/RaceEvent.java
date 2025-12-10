package org.domain.events;

import org.domain.Observer;
import org.domain.users.duck.SwimmingDuck;

import java.util.ArrayList;
import java.util.List;

public class RaceEvent extends Event<RaceEvent,SwimmingDuck> {

    private Double maxTime = 0.0;
    private List<Integer> distances = new ArrayList<>();
    private String name;

    public RaceEvent(List<SwimmingDuck> subscribers, String name) {
        super(new ArrayList<>());
        this.name = name;

        if (subscribers != null) {
            for (SwimmingDuck subscriber : subscribers) {
                this.addObserver(subscriber);
            }
        }
    }

    public RaceEvent(String name) {
        super(new ArrayList<>());
        this.name = name;
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

    @Override
    public String toString() {
        return super.toString() + " RaceEvent{" +
                "maxTime=" + maxTime +
                ", name='" + name + '\'' +
                ", distances=" + distances +
                '}';
    }
}