package org.service;

import javafx.application.Platform;
import org.domain.Observable;
import org.domain.Observer;
import org.domain.observer_events.MessageEvent;
import org.domain.users.User;
import org.domain.users.relationships.messages.Message;
import org.domain.users.relationships.messages.ReplyMessage;
import org.domain.users.relationships.notifications.MessageData;
import org.domain.users.relationships.notifications.Notification;
import org.domain.validators.Validator;
import org.repository.PagingRepository;
import org.service.utils.IdGenerator;
import org.utils.enums.actions.MessageAction;
import org.utils.enums.status.NotificationStatus;
import org.utils.enums.types.NotificationType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageService extends EntityService<Long, Message> implements Observable<MessageEvent, Observer<MessageEvent>> {
    private final List<Observer<MessageEvent>> observers = new CopyOnWriteArrayList<>();

    private AuthService authService;
    private NotificationService notificationService;

    public MessageService(Validator<Message> validator, PagingRepository<Long, Message> repository, IdGenerator<Long> idGenerator) {
        super(validator, repository, idGenerator);
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void sendMessage(User from, List<User> to, String text) {
        Message message = new Message(from, to, text);
        super.save(message);
        notifyObservers(new MessageEvent(MessageAction.SENT, List.of(message),authService.getCurrentUser() ));

        for(User u : to) {
            Notification notification = new Notification(
                    NotificationType.MESSAGE,
                    NotificationStatus.NEW,
                    from,
                    u
            );
            notification.setDescription("New message from " + authService.getCurrentUser().getUsername());
            notification.setData(new MessageData(message, MessageAction.SENT));

            notificationService.save(notification);
        }

    }

    public void replyMessage(User from, Message messageToReply, String text) {
        List<User> recipients = new ArrayList<>();
        recipients.add(messageToReply.getFrom());
        ReplyMessage reply = new ReplyMessage(from, recipients, text, messageToReply);
        super.save(reply);

        notifyObservers(new MessageEvent(MessageAction.REPLY, List.of(reply),authService.getCurrentUser() ));

        Notification notification = new Notification(
                NotificationType.MESSAGE,
                NotificationStatus.NEW,
                from,
                messageToReply.getFrom()
        );
        notification.setDescription("New message reply from " + authService.getCurrentUser().getUsername());
        notification.setData(new MessageData(reply, MessageAction.REPLY));

        notificationService.save(notification);


    }

    public List<Message> getConversation(User u1, User u2) {
        return StreamSupport.stream(super.findAll().spliterator(), false)
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
    public void addObserver(Observer<MessageEvent> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<MessageEvent> observer) {
        observers. remove(observer);
    }

    @Override
    public void notifyObservers(MessageEvent event) {
        for (Observer<MessageEvent> observer : observers) {
            Platform.runLater(() -> observer.update(event));
        }
    }
}