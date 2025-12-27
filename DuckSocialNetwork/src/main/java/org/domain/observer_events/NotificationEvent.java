package org.domain.observer_events;

import org.domain.users.User;
import org.domain.users.relationships.notifications.Notification;
import org.utils.enums.NotificationStatus;
import org.utils.enums.NotificationType;

import java.util.List;

public class NotificationEvent implements ObserverEvent {
    private final NotificationType type;
    private final NotificationStatus status;
    private final User user;

    public NotificationEvent(NotificationType type, NotificationStatus status, User user) {
        this.type = type;
        this.status = status;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public NotificationType getType() {
        return type;
    }
}