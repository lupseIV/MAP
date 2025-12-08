package org.service;

import org.domain.Observable;
import org.domain.Observer;
import org.domain.users.User;
import org.domain.users.relationships.messages.Message;
import org.domain.users.relationships.messages.ReplyMessage;
import org.domain.validators.Validator;
import org.repository.PagingRepository;
import org.service.utils.IdGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageService extends EntityService<Long, Message> implements Observable<Observer> {
    private NotificationService notificationService;
    private List<Observer> observers = new CopyOnWriteArrayList<>();

    public MessageService(Validator<Message> validator, PagingRepository<Long, Message> messageRepository, IdGenerator<Long> idGenerator) {
        super(validator, messageRepository, idGenerator);
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void sendMessage(User from, List<User> to, String text) {
        Message message = new Message(from, to, text);
        save(message);
        
        // Create notification for each recipient
        if (notificationService != null) {
            for (User recipient : to) {
                String preview = text.length() > 50 ? text.substring(0, 50) + "..." : text;
                notificationService.createNotification(recipient, from, preview);
            }
        }
        
        // Notify observers that a new message was sent
        notifyObservers();
    }

    public void replyMessage(User from, Message messageToReply, String text) {
        List<User> recipients = new ArrayList<>();
        recipients.add(messageToReply.getFrom());
        ReplyMessage reply = new ReplyMessage(from, recipients, text, messageToReply);
        save(reply);
        
        // Create notification for the original sender
        if (notificationService != null) {
            String preview = text.length() > 50 ? text.substring(0, 50) + "..." : text;
            notificationService.createNotification(messageToReply.getFrom(), from, preview);
        }
        
        // Notify observers that a new message was sent
        notifyObservers();
    }

    public List<Message> getConversation(User u1, User u2) {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .filter(m -> isPartofConversation(m, u1, u2))
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toList());
    }

    private boolean isPartofConversation(Message m, User u1, User u2) {
        boolean fromU1toU2 = m.getFrom().equals(u1) && m.getTo().contains(u2);
        boolean fromU2toU1 = m.getFrom().equals(u2) && m.getTo().contains(u1);
        return fromU1toU2 || fromU2toU1;
    }

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}