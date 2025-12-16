package org.domain.users.relationships.notifications;

import org.domain.users.User;
import org.domain.users.relationships.Friendship;
import org.utils.enums.NotificationStatus;
import org.utils.enums.NotificationType;

public class FriendRequestNotification extends Notification{
    private User from;
    private User to;
    private Friendship friendship;


    public FriendRequestNotification(User from, User to, Friendship friendship) {
        super(NotificationType.FRIEND_REQUEST, NotificationStatus.NEW);
        super.setMessage("Friend Request Notification");
        this.from = from;
        this.to = to;
        this.friendship = friendship;
    }

    public FriendRequestNotification(Long id, User from, User to, Friendship friendship) {
        this(from, to, friendship);
        this.setId(id);
    }

    public Friendship getFriendship() {
        return friendship;
    }

    public void setFriendship(Friendship friendship) {
        this.friendship = friendship;
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
