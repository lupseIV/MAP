package org.domain.observer_events;

import org.domain.users.User;
import org.domain.users.relationships.notifications.Notification;
import org.utils.enums.actions.FriendRequestAction;
import org.utils.enums.status.NotificationStatus;
import org.utils.enums.types.NotificationType;

import java.util.List;

public class FriendRequestEvent extends NotificationEvent implements ObserverEvent {
    private final FriendRequestAction action;
    private final List<Notification> messages;

    public FriendRequestEvent(FriendRequestAction action, List<Notification> messages, User user) {
        super(NotificationType.FRIEND_REQUEST, NotificationStatus.NEW, user);
        this.action = action;
        this.messages = messages;
    }

    public List<Notification> getMessages() {
        return messages;
    }

    public FriendRequestAction getAction() {
        return action;
    }
}