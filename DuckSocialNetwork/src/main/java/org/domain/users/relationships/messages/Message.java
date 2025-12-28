package org.domain.users.relationships.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.domain.Entity;
import org.domain.users.User;
import org.utils.enums.status.MessageStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ReplyMessage.class, name = "org.domain.users.relationships.messages.ReplyMessage")
})
public class Message extends Entity<Long> {
    private User from;
    private List<User> to;
    private String message;
    private LocalDateTime date;
    private MessageStatus status;

    public Message() {
    }

    public Message(User from, List<User> to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = LocalDateTime.now();
        this.status = MessageStatus.NEW;
    }

    public Message(Long id, User from, List<User> to, String message, LocalDateTime date, MessageStatus status) {
        this.setId(id);
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.status = status;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public List<User> getTo() {
        return to;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "From: " + from.getId() + " | " + message + " | " + date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        if (!super.equals(o)) return false;
        Message message1 = (Message) o;
        return Objects.equals(getFrom(), message1.getFrom()) &&
                Objects.equals(getMessage(), message1.getMessage()) &&
                Objects.equals(getDate(), message1.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getFrom(), getMessage(), getDate());
    }
}