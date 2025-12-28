package org.domain.observer_events;

import org.domain.users.User;
import org.domain.users.relationships.notifications.Notification;
import org.utils.enums.status.FriendRequestStatus;
import org.utils.enums.status.NotificationStatus;
import org.utils.enums.types.NotificationType;

import java.util.List;

public class FriendRequestEvent extends NotificationEvent implements ObserverEvent {
    private final FriendRequestStatus status;
    private final List<Notification> messages;

    public FriendRequestEvent(FriendRequestStatus status, List<Notification> messages, User user) {
        super(NotificationType.FRIEND_REQUEST, NotificationStatus.NEW, user);
        this.status = status;
        this.messages = messages;
    }

    public List<Notification> getMessages() {
        return messages;
    }

    public FriendRequestStatus getFriendRequestStatus() {
        return status;
    }
}