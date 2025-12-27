package org.domain.events;

import org.domain.users.duck.SwimmingDuck;

import java.util.List;

public class UpdateRaceEvent {
    private RaceEvent raceEvent;
    private List<SwimmingDuck> ducks;

    public UpdateRaceEvent(RaceEvent raceEvent, List<SwimmingDuck> duck) {
        this.raceEvent = raceEvent;
        this.ducks = duck;
    }

    public RaceEvent getRaceEvent() {
        return raceEvent;
    }

    public void setRaceEvent(RaceEvent raceEvent) {
        this.raceEvent = raceEvent;
    }

    public List<SwimmingDuck> getDuck() {
        return ducks;
    }

    public void setDuck(List<SwimmingDuck> duck) {
        this.ducks = duck;
    }
}
