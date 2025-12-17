package org.domain.events;

import org.domain.users.relationships.notifications.FriendNotification;
import org.utils.enums.NotificationStatus;
import org.utils.enums.NotificationType;

import java.util.List;

public class AddFriendEvent {
    private final NotificationType type;
    private NotificationStatus status;
    private final List<FriendNotification> messages;

    public AddFriendEvent(NotificationType type, NotificationStatus status, List<FriendNotification> messages) {
        this.type = type;
        this.status = status;
        this.messages = messages;
    }

    public NotificationType getType() {
        return type;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public List<FriendNotification> getMessages() {
        return messages;
    }
}
