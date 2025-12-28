package org.domain.observer_events;

import org.domain.users.User;
import org.domain.users.relationships.messages.Message;
import org.utils.enums.status.MessageStatus;
import org.utils.enums.status.NotificationStatus;
import org.utils.enums.types.NotificationType;

import java.util.List;

public class MessageEvent extends NotificationEvent {
    private final List<Message> messages;
    private final MessageStatus status;

    public MessageEvent( User user, List<Message> messages, MessageStatus messageStatus) {
        super(NotificationType.MESSAGE, NotificationStatus.NEW, user);
        this.messages = messages;
        this.status = messageStatus;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public MessageStatus getMessageStatus() {
        return status;
    }
}
