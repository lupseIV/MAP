package org.domain.users.relationships.messages;

import org.domain.Entity;
import org.domain.users.User;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long> {
    private User from;
    private List<User> to;
    private String body;
    private LocalDateTime date;

    public Message(User from, List<User> to, String body, LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.body = body;
        this.date = date;
    }

    public User getFrom() {
        return from;
    }

    public List<User> getTo() {
        return to;
    }

    public String getBody() {
        return body;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", to=" + to +
                ", body='" + body + '\'' +
                ", date=" + date +
                '}';
    }
}
