package org.domain.users.relationships.notifications;

import org.domain.Entity;
import org.domain.users.User;
import org.utils.enums.status.NotificationStatus;
import org.utils.enums.types.NotificationType;

public class Notification extends Entity<Long> {
    private NotificationType type;
    private NotificationStatus status;
    private String description;
    private User sender;
    private User receiver;
    private NotificationData data;


    public Notification(NotificationType type, NotificationStatus status, User sender, User receiver) {
        this.type = type;
        this.status = status;
        this.sender = sender;
        this.receiver = receiver;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public NotificationData getData() {
        return data;
    }

    public void setData(NotificationData data) {
        this.data = data;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
