package org.domain.events;

import org.domain.users.User;
import org.domain.users.relationships.notifications.FriendNotification;
import org.utils.enums.NotificationStatus;
import org.utils.enums.NotificationType;

import java.util.List;

public class AddFriendEvent {
    private final NotificationType type;
    private NotificationStatus status;
    private final List<FriendNotification> messages;
    private final User user;

    public AddFriendEvent(NotificationType type, NotificationStatus status, List<FriendNotification> messages, User user) {
        this.type = type;
        this.status = status;
        this.messages = messages;
        this.user = user;
    }


    public User getUser() {
        return user;
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
