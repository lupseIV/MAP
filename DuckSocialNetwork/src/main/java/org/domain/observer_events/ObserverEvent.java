package org.domain.observer_events;

import org.domain.users.User;
import org.utils.enums.NotificationStatus;
import org.utils.enums.NotificationType;

public interface ObserverEvent {
    NotificationType getType();
    NotificationStatus getStatus();
    User getUser();
}
