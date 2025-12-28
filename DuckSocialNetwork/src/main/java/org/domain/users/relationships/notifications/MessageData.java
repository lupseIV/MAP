package org.domain.users.relationships.notifications;

import org.domain.users.relationships.messages.Message;
import org.utils.enums.actions.MessageAction;

public class MessageData implements NotificationData{
    private final Message message;
    private final MessageAction action;

    public MessageData(Message message, MessageAction action) {
        this.message = message;
        this.action = action;
    }

    public MessageAction getAction() {
        return action;
    }
    public Message getMessage() {
        return message;
    }
}
