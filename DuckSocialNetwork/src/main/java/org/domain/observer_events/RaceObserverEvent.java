package org.domain.observer_events;

import org.domain.events.RaceEvent;
import org.domain.users.User;
import org.utils.enums.status.NotificationStatus;
import org.utils.enums.types.NotificationType;
import org.utils.enums.actions.RaceEventAction;

import java.util.List;

public class RaceObserverEvent extends NotificationEvent {
    private final RaceEventAction action;
    private final List<RaceEvent> raceEvents;

    public RaceObserverEvent(User user, List<RaceEvent> raceEvent, RaceEventAction action) {
        super(NotificationType.RACE_EVENT, NotificationStatus.NEW, user);
        this.raceEvents = raceEvent;
        this.action = action;
    }

    public List<RaceEvent> getRaceEvent() {
        return raceEvents;
    }

    public RaceEventAction getAction() {
        return action;
    }
}
