package org.domain.users.relationships;

import org.domain.Entity;
import org.domain.users.User;
import org.utils.enums.status.FriendRequestStatus;

import java.util.Objects;

public class Friendship extends Entity<Long> {
    private User user1;
    private User user2;
    private FriendRequestStatus status;

    public Friendship( User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        user1.addFriend(user2);
    }

    public Friendship(User user1, User user2, FriendRequestStatus status) {
        this.user1 = user1;
        this.user2 = user2;
        this.status = status;
    }


    public FriendRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    @Override
    public String toString() {
        return super.toString()+"Friendship{" +
                "user1=" + user1 +
                ", user2=" + user2 +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(user1, that.user1) && Objects.equals(user2, that.user2) ||
                Objects.equals(user1, that.user2) && Objects.equals(user2, that.user1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), user1, user2);
    }
}
