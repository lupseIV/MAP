package org.domain.observer_events;

import org.domain.users.User;
import org.domain.users.relationships.messages.Message;
import org.utils.enums.actions.MessageAction;
import org.utils.enums.status.NotificationStatus;
import org.utils.enums.types.NotificationType;

import java.util.List;

public class MessageEvent extends NotificationEvent {
    private final List<Message> messages;
    private final MessageAction action;

    public MessageEvent( MessageAction action , List<Message> messages,User user) {
        super(NotificationType.MESSAGE, NotificationStatus.NEW, user);
        this.messages = messages;
        this.action = action;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public MessageAction getAction() {
        return action;
    }
}
