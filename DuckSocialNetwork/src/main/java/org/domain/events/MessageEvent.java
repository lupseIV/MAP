package org.domain.events;

import org.domain.users.relationships.messages.Message;
import org.utils.enums.MessageType;

import java.util.List;

public class MessageEvent {
    private final MessageType type;
    private final List<Message> messages;

    public MessageEvent(MessageType type, List<Message> messages) {
        this.type = type;
        this.messages = messages;
    }

    public MessageType getType() {
        return type;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
