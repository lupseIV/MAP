package org.ui.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.util.Callback;
import org.domain.Observer;
import org.domain.events.AddFriendEvent;
import org.domain.users.User;
import org.domain.users.relationships.messages.Message;
import org.domain.users.relationships.notifications.FriendRequestNotification;
import org.service.AuthService;
import org.service.NotificationService;
import org.utils.enums.NotificationStatus;

import java.util.List;

public class NotificationController implements Observer<AddFriendEvent> {

    private NotificationService notificationService;
    private AuthService authService;
    private User currentUser;

    @FXML private ListView<FriendRequestNotification> notificationListView;

    public void setServices(NotificationService notificationService, AuthService authService) {
        this.notificationService = notificationService;
        this.authService = authService;

        currentUser = authService.getCurrentUser();

        notificationService.addObserver(this);

        loadNotifications();
    }

    private void loadNotifications() {
        if (currentUser != null) {
            List<FriendRequestNotification> conversation = notificationService.findAll(currentUser);
            notificationListView.setItems(FXCollections.observableArrayList(conversation));
            notificationListView.scrollTo(conversation.size() - 1);
        }
    }

    @Override
    public void update(AddFriendEvent event) {
        if (event.getStatus() == NotificationStatus.NEW) {
            loadNotifications();
        }
    }

    @FXML
    public void initialize() {
        notificationListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<FriendRequestNotification> call(ListView<FriendRequestNotification> param) {
                return new ListCell<FriendRequestNotification>() {
                    @Override
                    protected void updateItem(FriendRequestNotification notification, boolean empty) {
                        super.updateItem(notification, empty);

                        if (empty || notification == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            User sender = notification.getFrom();
                            String senderName = (sender != null) ? sender.getUsername() : "Unknown";
                            String message = notification.getMessage();
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
