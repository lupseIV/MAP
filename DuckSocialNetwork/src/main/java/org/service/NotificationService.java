package org.service;

import javafx.application.Platform;
import org.domain.Observable;
import org.domain.Observer;
import org.domain.events.AddFriendEvent;
import org.domain.events.MessageEvent;
import org.domain.users.User;
import org.domain.users.relationships.Friendship;
import org.domain.users.relationships.notifications.FriendRequestNotification;
import org.domain.users.relationships.notifications.Notification;
import org.domain.validators.Validator;
import org.repository.PagingRepository;
import org.service.utils.IdGenerator;
import org.utils.enums.NotificationStatus;
import org.utils.enums.NotificationType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class NotificationService extends EntityService<Long, FriendRequestNotification> implements Observable<AddFriendEvent, Observer<AddFriendEvent>> {

    private final List<Observer<AddFriendEvent>> observers = new CopyOnWriteArrayList<>();


    public NotificationService(Validator<FriendRequestNotification> validator, PagingRepository<Long, FriendRequestNotification> repository,
                               IdGenerator<Long> idGenerator) {
        super(validator, repository, idGenerator);
    }

    @Override
    public FriendRequestNotification save(FriendRequestNotification entity) {
        notifyObservers(new AddFriendEvent(NotificationType.FRIEND_REQUEST, NotificationStatus.NEW, List.of(entity)));
        return super.save(entity);
    }

    @Override
    public FriendRequestNotification delete(Long aLong) {
        notifyObservers(new AddFriendEvent(NotificationType.FRIEND_REQUEST, NotificationStatus.DELETED, List.of(findOne(aLong))));

        return super.delete(aLong);
    }

    @Override
    public FriendRequestNotification update(FriendRequestNotification entity) {
        notifyObservers(new AddFriendEvent(NotificationType.FRIEND_REQUEST, NotificationStatus.READ, List.of(entity)));

        return super.update(entity);
    }

    @Override
    public void addObserver(Observer<AddFriendEvent> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<AddFriendEvent> observer) {
        observers. remove(observer);
    }

    @Override
    public void notifyObservers(AddFriendEvent event) {
        for (Observer<AddFriendEvent> observer : observers) {
            Platform.runLater(() -> observer.update(event));
        }
    }

    public List<FriendRequestNotification> findAll(User currentUser) {
        return StreamSupport.stream(super.findAll().spliterator(), false)
                .filter(notification -> notification.getTo().equals(currentUser))
                .collect(Collectors.toList());
    }
}
