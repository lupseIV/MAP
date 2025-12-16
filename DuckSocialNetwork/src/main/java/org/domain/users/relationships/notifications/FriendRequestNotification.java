package org.domain.users.relationships.notifications;

import org.domain.users.User;
import org.utils.enums.NotificationStatus;
import org.utils.enums.NotificationType;

public class FriendRequestNotification extends Notification{
    private User from;
    private User to;


    public FriendRequestNotification(User from, User to) {
        super(NotificationType.FRIEND_REQUEST, NotificationStatus.NEW);
        super.setMessage("Friend Request Notification");
        this.from = from;
        this.to = to;
    }

    public FriendRequestNotification(Long id, User from, User to) {
        this(from, to);
        this.setId(id);
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }
}
