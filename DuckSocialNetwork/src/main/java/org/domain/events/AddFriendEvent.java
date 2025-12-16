package org.domain.events;

import org.domain.users.User;
import org.domain.users.relationships.messages.Message;
import org.domain.users.relationships.notifications.FriendRequestNotification;
import org.utils.enums.NotificationStatus;
import org.utils.enums.NotificationType;

import java.util.List;

public class AddFriendEvent {
    private final NotificationType type;
    private NotificationStatus status;
    private final List<FriendRequestNotification> messages;

    public AddFriendEvent(NotificationType type, NotificationStatus status, List<FriendRequestNotification> messages) {
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

    public List<FriendRequestNotification> getMessages() {
        return messages;
    }
}
