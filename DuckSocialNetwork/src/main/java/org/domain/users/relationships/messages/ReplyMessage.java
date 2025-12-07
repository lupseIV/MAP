package org.domain.users.relationships.messages;

public class ReplyMessage {
    private Message message;

    public ReplyMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ReplyMessage{" +
                "message=" + message +
                '}';
    }
}
