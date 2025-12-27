package org.ui.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.domain.Observer;
import org.domain.observer_events.ObserverEvent;
import org.domain.users.User;
import org.domain.users.relationships.notifications.Notification;
import org.service.AuthService;
import org.service.NotificationService;
import org.utils.enums.NotificationStatus;

import java.util.List;

public class NotificationController implements Observer<ObserverEvent> {

    private NotificationService notificationService;
    private AuthService authService;
    private User currentUser;

    @FXML private ListView<Notification> notificationListView;

    public void setServices(NotificationService notificationService, AuthService authService) {
        this.notificationService = notificationService;
        this.authService = authService;

        currentUser = authService.getCurrentUser();

        notificationService.addObserver(this);

        loadNotifications();
    }

    private void loadNotifications() {
        if (currentUser != null) {
            List<Notification> conversation = notificationService.findAll(currentUser);
            notificationListView.setItems(FXCollections.observableArrayList(conversation));
            notificationListView.scrollTo(conversation.size() - 1);
        }
    }

    @Override
    public void update(ObserverEvent event) {
        if (event.getStatus() == NotificationStatus.NEW) {
            loadNotifications();
        }
    }

    @FXML
    public void initialize() {
        notificationListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Notification> call(ListView<Notification> param) {
                return new ListCell<Notification>() {
                    @Override
                    protected void updateItem(Notification notification, boolean empty) {
                        super.updateItem(notification, empty);

                        if (empty || notification == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            User sender = notification.getSender();
                            String senderName = (sender != null) ? sender.getUsername() : "Unknown";
                            String message = notification.getDescription();
                            String status = notification.getStatus() != null ? notification.getStatus().toString() : "";

                            setText(String.format("From: %s\nMessage: %s\nStatus: %s", senderName, message, status));
                        }
                    }
                };
            }
        });
    }

    @FXML
    public void markAllAsRead() {
        notificationService.markAllAsRead(currentUser);
        loadNotifications();
    }
}
