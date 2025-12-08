package org.service;

import org.domain.users.User;
import org.domain.users.relationships.messages.Notification;
import org.domain.validators.Validator;
import org.repository.PagingRepository;
import org.service.utils.IdGenerator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class NotificationService extends EntityService<Long, Notification> {

    public NotificationService(Validator<Notification> validator, PagingRepository<Long, Notification> notificationRepository, IdGenerator<Long> idGenerator) {
        super(validator, notificationRepository, idGenerator);
    }

    public void createNotification(User recipient, User sender, String messagePreview) {
        Notification notification = new Notification(recipient, sender, messagePreview);
        save(notification);
    }

    public List<Notification> getUnreadNotifications(User user) {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .filter(n -> n.getRecipient().equals(user) && !n.isRead())
                .collect(Collectors.toList());
    }

    public List<Notification> getAllNotifications(User user) {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .filter(n -> n.getRecipient().equals(user))
                .collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId) {
        Notification notification = repository.findOne(notificationId);
        if (notification != null) {
            notification.setRead(true);
            repository.update(notification);
        }
    }

    public void markAllAsRead(User user) {
        StreamSupport.stream(repository.findAll().spliterator(), false)
                .filter(n -> n.getRecipient().equals(user) && !n.isRead())
                .forEach(n -> {
                    n.setRead(true);
                    repository.update(n);
                });
    }

    public int getUnreadCount(User user) {
        return (int) StreamSupport.stream(repository.findAll().spliterator(), false)
                .filter(n -> n.getRecipient().equals(user) && !n.isRead())
                .count();
    }
}
