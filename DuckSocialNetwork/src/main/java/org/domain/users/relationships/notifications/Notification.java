package org.domain.users.relationships.notifications;

import org.domain.Entity;
import org.utils.enums.NotificationStatus;
import org.utils.enums.NotificationType;

public abstract class Notification extends Entity<Long> {
    private NotificationType type;
    private NotificationStatus status;
    private String message;

    public Notification(NotificationType type, NotificationStatus status) {
        this.type = type;
        this.status = status;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
