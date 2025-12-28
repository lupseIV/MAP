package org.service;

import javafx.application.Platform;
import org.domain.Observable;
import org.domain.Observer;
import org.domain.observer_events.FriendRequestEvent;
import org.domain.observer_events.NotificationEvent;
import org.domain.observer_events.ObserverEvent;
import org.domain.users.User;
import org.domain.users.relationships.Friendship;
import org.domain.users.relationships.notifications.FriendRequestData;
import org.domain.users.relationships.notifications.Notification;
import org.domain.validators.Validator;
import org.repository.PagingRepository;
import org.service.utils.IdGenerator;
import org.utils.enums.FriendRequestStatus;
import org.utils.enums.NotificationStatus;
import org.utils.enums.NotificationType;
import org.utils.enums.status.FriendRequestStatus;
import org.utils.enums.status.NotificationStatus;
import org.utils.enums.types.NotificationType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class NotificationService extends EntityService<Long, Notification> implements Observable<ObserverEvent, Observer<ObserverEvent>> {

    private final List<Observer<ObserverEvent>> observers = new CopyOnWriteArrayList<>();
    private AuthService authService;

    public NotificationService(Validator<Notification> validator, PagingRepository<Long, Notification> repository,
                               IdGenerator<Long> idGenerator) {
        super(validator, repository, idGenerator);
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Notification save(Notification entity) {
        var res = super.save(entity);

        ObserverEvent event = createEventFromNotification(entity);
        if (event != null) {
            notifyObservers(event);
        }

        return res;
    }

    private ObserverEvent createEventFromNotification(Notification notification) {
        if (notification.getType() == NotificationType.FRIEND_REQUEST) {
            if (notification.getData() instanceof FriendRequestData data) {
                FriendRequestStatus status = FriendRequestStatus.PENDING;
                return new FriendRequestEvent( status, List.of(notification), authService.getCurrentUser());
            }
        }
        // TODO: handle other notification types
        return null;
    }

    @Override
    public Notification delete(Long aLong) {
        var res = super.delete(aLong);
        return res;
    }

    @Override
    public Notification update(Notification entity) {
        return super.update(entity);
    }

    @Override
    public void addObserver(Observer<ObserverEvent> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<ObserverEvent> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(ObserverEvent event) {
        for (Observer<ObserverEvent> observer : observers) {
            Platform.runLater(() -> observer.update(event));
        }
    }

    public List<Notification> findAll(User currentUser) {
        return StreamSupport.stream(super.findAll().spliterator(), false)
                .filter(notification -> notification.getReceiver().equals(currentUser))
                .collect(Collectors.toList());
    }

    public Notification findOne(Friendship friendship) {
        return StreamSupport.stream(findAll().spliterator(), false)
                .filter(n -> {
                    if (n.getData() instanceof FriendRequestData) {
                        FriendRequestData data = (FriendRequestData) n.getData();
                        return data.getFriendship().equals(friendship);
                    }
                    return false;
                })
                .findFirst()
                .orElse(null);
    }

    public Notification delete(Friendship friendship) {
        Notification n = findOne(friendship);
        if (n != null) {
            return delete(n.getId());
        }
        return null;
    }

    public void markAllAsRead(User currentUser) {
        StreamSupport.stream(repository.findAll().spliterator(), false)
                .filter(notification -> notification.getReceiver().equals(currentUser))
                .filter(notification -> notification.getStatus() == NotificationStatus.NEW)
                .forEach(notification -> {
                    notification.setStatus(NotificationStatus.READ);
                    repository.update(notification);
                });
        notifyObservers(new NotificationEvent(NotificationType.SYSTEM_ALERT, NotificationStatus.READ, authService.getCurrentUser()));
    }
}
