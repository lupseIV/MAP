package org.domain.observer_events;

import org.domain.users.User;
import org.utils.enums.status.NotificationStatus;
import org.utils.enums.types.NotificationType;

public interface ObserverEvent {
    NotificationType getType();
    NotificationStatus getStatus();
    User getUser();
}
