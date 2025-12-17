package org.service;

import javafx.application.Platform;
import org.domain.Observable;
import org.domain.Observer;
import org.domain.events.AddFriendEvent;
import org.domain.users.User;
import org.domain.users.relationships.Friendship;
import org.domain.users.relationships.notifications.FriendNotification;
import org.domain.validators.Validator;
import org.repository.PagingRepository;
import org.service.utils.IdGenerator;
import org.utils.enums.NotificationStatus;
import org.utils.enums.NotificationType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class NotificationService extends EntityService<Long, FriendNotification> implements Observable<AddFriendEvent, Observer<AddFriendEvent>> {

    private final List<Observer<AddFriendEvent>> observers = new CopyOnWriteArrayList<>();
    private AuthService authService;

    public NotificationService(Validator<FriendNotification> validator, PagingRepository<Long, FriendNotification> repository,
                               IdGenerator<Long> idGenerator) {
        super(validator, repository, idGenerator);
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public FriendNotification save(FriendNotification entity) {
        var res =super.save(entity);
        notifyObservers(new AddFriendEvent(NotificationType.FRIEND_REQUEST, NotificationStatus.NEW, List.of(entity), authService.getCurrentUser()));
        return res;
    }

    @Override
    public FriendNotification delete(Long aLong) {
        var res =  super.delete(aLong);
        notifyObservers(new AddFriendEvent(NotificationType.FRIEND_REQUEST, NotificationStatus.DELETED, List.of(findOne(aLong)),authService.getCurrentUser()));

        return res;
    }

    @Override
    public FriendNotification update(FriendNotification entity) {
        notifyObservers(new AddFriendEvent(NotificationType.FRIEND_REQUEST, NotificationStatus.READ, List.of(entity), authService.getCurrentUser()));

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

    public List<FriendNotification> findAll(User currentUser) {
        return StreamSupport.stream(super.findAll().spliterator(), false)
                .filter(notification -> notification.getTo().equals(currentUser))
                .collect(Collectors.toList());
    }

    public FriendNotification findOne(Friendship friendship) {
        return StreamSupport.stream(findAll().spliterator(),false)
                .filter(n ->
                        friendship.equals(n.getFriendship())).findFirst().orElse(null);
    }

    public FriendNotification delete(Friendship friendship) {
        return delete(findOne(friendship).getId());
    }

    public void markAllAsRead(User currentUser) {
        StreamSupport.stream(repository.findAll().spliterator(), false)
                .filter(notification -> notification.getTo().equals(currentUser))
                .filter(notification -> notification.getStatus() == NotificationStatus.NEW)
                .forEach(notification -> {
                    notification.setStatus(NotificationStatus.READ);
                    repository.update(notification);
                });
    }
}
