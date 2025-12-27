package org.domain.users.relationships.notifications;

import org.domain.users.relationships.Friendship;
import org.utils.enums.FriendRequestStatus;

public class FriendRequestData implements NotificationData {
    private Friendship friendship;
    private FriendRequestStatus action;

    public FriendRequestData() {}

    public FriendRequestData(Friendship friendship, FriendRequestStatus action) {
        this.friendship = friendship;
        this.action = action;
    }

    public FriendRequestStatus getAction() {
        return action;
    }

    public void setAction(FriendRequestStatus action) {
        this.action = action;
    }

    public Friendship getFriendship() {
        return friendship;
    }

    public void setFriendship(Friendship friendship) {
        this.friendship = friendship;
    }

    @Override
    public String toString() {
        return "FriendRequestData{" +
                "friendship=" + friendship +
                '}';
    }
}