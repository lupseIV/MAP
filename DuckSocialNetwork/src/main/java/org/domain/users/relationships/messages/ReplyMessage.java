package org.domain.users.relationships.messages;

import org.domain.users.User;
import org.utils.enums.status.MessageStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ReplyMessage extends Message {
    private Message repliedMessage;

    public ReplyMessage(User from, List<User> to, String message, Message repliedMessage) {
        super(from, to, message);
        this.repliedMessage = repliedMessage;
    }

    public ReplyMessage(Long id, User from, List<User> to, String message, LocalDateTime date, Message repliedMessage, MessageStatus status) {
        super(id, from, to, message, date, status);
        this.repliedMessage = repliedMessage;
    }

    public Message getRepliedMessage() {
        return repliedMessage;
    }

    public void setRepliedMessage(Message repliedMessage) {
        this.repliedMessage = repliedMessage;
    }

    @Override
    public String toString() {
        String replyText = (repliedMessage != null) ? " (Reply to: " + repliedMessage.getMessage() + ")" : "";
        return super.toString() + replyText;
    }
}