package org.domain.users.relationships.messages;

import org.domain.Entity;
import org.domain.users.User;

import java.time.LocalDateTime;
import java.util.Objects;

public class Notification extends Entity<Long> {
    private User recipient;
    private User sender;
    private String messagePreview;
    private LocalDateTime timestamp;
    private boolean read;

    public Notification(User recipient, User sender, String messagePreview) {
        this.recipient = recipient;
        this.sender = sender;
        this.messagePreview = messagePreview;
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }

    public Notification(Long id, User recipient, User sender, String messagePreview, LocalDateTime timestamp, boolean read) {
        this.setId(id);
        this.recipient = recipient;
        this.sender = sender;
        this.messagePreview = messagePreview;
        this.timestamp = timestamp;
        this.read = read;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getMessagePreview() {
        return messagePreview;
    }

    public void setMessagePreview(String messagePreview) {
        this.messagePreview = messagePreview;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    public String toString() {
        return "New message from " + sender.getUsername() + ": " + messagePreview;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        if (!super.equals(o)) return false;
        Notification that = (Notification) o;
        return Objects.equals(recipient, that.recipient) &&
                Objects.equals(sender, that.sender) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), recipient, sender, timestamp);
    }
}
