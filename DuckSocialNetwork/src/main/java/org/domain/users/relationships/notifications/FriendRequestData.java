package org.domain.users.relationships.notifications;

import org.domain.users.relationships.Friendship;
import org.utils.enums.actions.FriendRequestAction;

public class FriendRequestData implements NotificationData {
    private Friendship friendship;
    private FriendRequestAction action;

    public FriendRequestData() {}

    public FriendRequestData(Friendship friendship, FriendRequestAction action) {
        this.friendship = friendship;
        this.action = action;
    }

    public FriendRequestAction getAction() {
        return action;
    }

    public void setAction(FriendRequestAction action) {
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