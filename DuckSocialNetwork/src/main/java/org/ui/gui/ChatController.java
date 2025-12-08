package org.ui.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.domain.Observer;
import org.domain.users.User;
import org.domain.users.relationships.messages.Message;
import org.domain.users.relationships.messages.ReplyMessage;
import org.service.AuthService;
import org.service.MessageService;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatController implements Observer {
    private MessageService messageService;
    private AuthService authService;
    private User currentUser;
    private User chatPartner;
    private Timer refreshTimer;
    private int lastMessageCount = 0;

    @FXML private Label recipientLabel;
    @FXML private ListView<Message> messageListView;
    @FXML private TextArea messageInput;

    public void setServices(MessageService messageService, AuthService authService, User chatPartner) {
        this.messageService = messageService;
        this.authService = authService;
        this.currentUser = authService.getCurrentUser();
        this.chatPartner = chatPartner;

        recipientLabel.setText("Chat with " + chatPartner.getEmail());
        
        // Register as observer
        messageService.addObserver(this);
        
        // Start auto-refresh timer (check every 2 seconds)
        startAutoRefresh();
        
        loadMessages();
    }

    @FXML
    public void initialize() {
        messageListView.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {
            @Override
            public ListCell<Message> call(ListView<Message> param) {
                return new ListCell<Message>() {
                    @Override
                    protected void updateItem(Message msg, boolean empty) {
                        super.updateItem(msg, empty);
                        if (empty || msg == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            boolean isMe = msg.getFrom().equals(currentUser);
                            String senderName = isMe ? "Me" : msg.getFrom().getEmail();
                            String replyText = "";

                            if (msg instanceof ReplyMessage) {
                                Message original = ((ReplyMessage) msg).getRepliedMessage();
                                if (original != null) {
                                    replyText = " [Replying to: " + original.getMessage() + "]\n";
                                }
                            }

                            setText(senderName + ":\n" + replyText + msg.getMessage());

                            // Stilizare simplă pentru a diferenția expeditorul
                            if (isMe) {
                                setStyle("-fx-alignment: CENTER-RIGHT; -fx-background-color: #e3f2fd;");
                            } else {
                                setStyle("-fx-alignment: CENTER-LEFT; -fx-background-color: #f5f5f5;");
                            }
                        }
                    }
                };
            }
        });
    }

    private void loadMessages() {
        if (currentUser != null && chatPartner != null) {
            List<Message> conversation = messageService.getConversation(currentUser, chatPartner);
            
            // Only update if message count changed to avoid unnecessary UI updates
            if (conversation.size() != lastMessageCount) {
                lastMessageCount = conversation.size();
                Platform.runLater(() -> {
                    messageListView.setItems(FXCollections.observableArrayList(conversation));
                    if (!conversation.isEmpty()) {
                        messageListView.scrollTo(conversation.size() - 1);
                    }
                });
            }
        }
    }
    
    private void startAutoRefresh() {
        refreshTimer = new Timer(true); // Daemon thread
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                loadMessages();
            }
        }, 0, 2000); // Check every 2 seconds
    }
    
    public void cleanup() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
        if (messageService != null) {
            messageService.removeObserver(this);
        }
    }

    @FXML
    private void handleSendMessage() {
        String text = messageInput.getText();
        if (text.isEmpty()) return;

        messageService.sendMessage(currentUser, Collections.singletonList(chatPartner), text);
        messageInput.clear();
        // Observer pattern will trigger loadMessages() via update()
    }

    @FXML
    private void handleReplyMessage() {
        Message selectedMessage = messageListView.getSelectionModel().getSelectedItem();
        if (selectedMessage == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select a message to reply to.");
            alert.show();
            return;
        }

        String text = messageInput.getText();
        if (text.isEmpty()) return;

        messageService.replyMessage(currentUser, selectedMessage, text);
        messageInput.clear();
        // Observer pattern will trigger loadMessages() via update()
    }
    
    @Override
    public void update() {
        // Called by MessageService when a new message is sent
        // Already wrapped in Platform.runLater to ensure JavaFX thread safety
        loadMessages();
    }
}