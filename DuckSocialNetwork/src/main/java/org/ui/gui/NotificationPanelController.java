package org.ui.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.domain.users.relationships.messages.Notification;
import org.service.AuthService;
import org.service.NotificationService;

import java.util.List;

public class NotificationPanelController {
    
    @FXML
    private ListView<Notification> notificationListView;
    
    @FXML
    private Label notificationCountLabel;
    
    private NotificationService notificationService;
    private AuthService authService;
    
    @FXML
    private void initialize() {
        notificationListView.setCellFactory(lv -> new javafx.scene.control.ListCell<Notification>() {
            @Override
            protected void updateItem(Notification notification, boolean empty) {
                super.updateItem(notification, empty);
                if (empty || notification == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(notification.toString());
                    if (!notification.isRead()) {
                        setStyle("-fx-background-color: #fffacd; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        
        notificationListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Notification selected = notificationListView.getSelectionModel().getSelectedItem();
                if (selected != null && !selected.isRead() && notificationService != null) {
                    notificationService.markAsRead(selected.getId());
                    loadNotifications();
                }
            }
        });
    }
    
    public void setServices(NotificationService notificationService, AuthService authService) {
        this.notificationService = notificationService;
        this.authService = authService;
        loadNotifications();
    }
    
    public void loadNotifications() {
        if (authService != null && authService.getCurrentUser() != null && notificationService != null) {
            List<Notification> notifications = notificationService.getAllNotifications(authService.getCurrentUser());
            notificationListView.setItems(FXCollections.observableArrayList(notifications));
            
            int unreadCount = notificationService.getUnreadCount(authService.getCurrentUser());
            notificationCountLabel.setText("Notifications (" + unreadCount + " unread)");
        }
    }
    
    @FXML
    private void handleMarkAllRead() {
        if (authService != null && authService.getCurrentUser() != null && notificationService != null) {
            notificationService.markAllAsRead(authService.getCurrentUser());
            loadNotifications();
        }
    }
}
