package org.domain.users.relationships.notifications;

import org.domain.events.RaceEvent;
import org.domain.users.User;
import org.utils.enums.actions.RaceEventAction;

public class RaceEventData implements NotificationData {
    private RaceEvent event;
    private User user;
    private RaceEventAction action;

    // No-arg constructor for serialization
    public RaceEventData() {}

    public RaceEventData(RaceEvent event, User user, RaceEventAction action) {
        this.event = event;
        this.user = user;
        this.action = action;
    }

    public RaceEvent getEvent() {
        return event;
    }

    public void setEvent(RaceEvent event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RaceEventAction getAction() {
        return action;
    }

    public void setAction(RaceEventAction action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "RaceEventData{" +
                "event=" + event +
                ", user=" + user +
                ", action='" + action + '\'' +
                '}';
    }
}