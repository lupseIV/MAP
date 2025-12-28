package org.domain.observer_events;

import org.domain.events.RaceEvent;
import org.domain.users.User;
import org.utils.enums.status.NotificationStatus;
import org.utils.enums.types.NotificationType;
import org.utils.enums.status.RaceEventStatus;

import java.util.List;

public class RaceObserverEvent extends NotificationEvent {
    private final RaceEventStatus status;
    private final List<RaceEvent> raceEvents;

    public RaceObserverEvent(User user, List<RaceEvent> raceEvent, RaceEventStatus status) {
        super(NotificationType.RACE_EVENT, NotificationStatus.NEW, user);
        this.raceEvents = raceEvent;
        this.status = status;
    }

    public List<RaceEvent> getRaceEvent() {
        return raceEvents;
    }

    public RaceEventStatus getRaceStatus() {
        return status;
    }
}
