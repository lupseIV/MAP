package org.domain.users.relationships.notifications;

import org.domain.events.RaceEvent;
import org.domain.users.User;
import org.domain.users.duck.SwimmingDuck;
import org.domain.users.relationships.Friendship;
import org.utils.enums.NotificationStatus;
import org.utils.enums.NotificationType;

public class RaceEventNotification extends Notification{
    private RaceEvent raceEvent;
    private SwimmingDuck subscriber;

    public RaceEventNotification(SwimmingDuck duck, RaceEvent raceEvent) {
        super(NotificationType.RACE_EVENT, NotificationStatus.NEW);
        super.setMessage("Race Event Notification");
        subscriber = duck;
        this.raceEvent = raceEvent;
    }

    public RaceEventNotification(Long id, SwimmingDuck subscriber, RaceEvent raceEvent) {
        this(subscriber, raceEvent);
        this.setId(id);
    }

    public void setRaceEvent(RaceEvent raceEvent) {
        this.raceEvent = raceEvent;
    }

    public SwimmingDuck getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(SwimmingDuck subscriber) {
        this.subscriber = subscriber;
    }
}
