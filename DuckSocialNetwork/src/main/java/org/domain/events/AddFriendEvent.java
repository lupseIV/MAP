package org.domain.events;

import org.domain.users.User;

public class AddFriendEvent {
    private User from;
    private User to;

    public AddFriendEvent(User from, User to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "AddFriendEvent{" +
                "from=" + from +
                ", to=" + to +
                '}';
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
