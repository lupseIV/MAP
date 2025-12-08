package org.service;

import org.domain.users.User;
import org.domain.users.relationships.messages.Message;
import org.domain.users.relationships.messages.ReplyMessage;
import org.repository.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageService {
    private final Repository<Long, Message> messageRepository;

    public MessageService(Repository<Long, Message> messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void sendMessage(User from, List<User> to, String text) {
        Message message = new Message(from, to, text);
        messageRepository.save(message);
    }

    public void replyMessage(User from, Message messageToReply, String text) {
        List<User> recipients = new ArrayList<>();
        recipients.add(messageToReply.getFrom());
        ReplyMessage reply = new ReplyMessage(from, recipients, text, messageToReply);
        messageRepository.save(reply);
    }

    public List<Message> getConversation(User u1, User u2) {
        return StreamSupport.stream(messageRepository.findAll().spliterator(), false)
                .filter(m -> isPartofConversation(m, u1, u2))
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toList());
    }

    private boolean isPartofConversation(Message m, User u1, User u2) {
        boolean fromU1toU2 = m.getFrom().equals(u1) && m.getTo().contains(u2);
        boolean fromU2toU1 = m.getFrom().equals(u2) && m.getTo().contains(u1);
        return fromU1toU2 || fromU2toU1;
    }
}