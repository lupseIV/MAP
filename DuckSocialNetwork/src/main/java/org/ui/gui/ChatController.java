package org.ui.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.domain.Observer;
import org.domain.observer_events.MessageEvent;
import org.domain.users.User;
import org.domain.users.relationships.messages.Message;
import org.domain.users.relationships.messages.ReplyMessage;
import org.service.AuthService;
import org.service.MessageService;
import org.utils.enums.status.MessageStatus;
import org.utils.enums.types.NotificationType;

import java.util.Collections;
import java.util.List;

public class ChatController implements Observer<MessageEvent> {
    private MessageService messageService;
    private AuthService authService;
    private User currentUser;
    private User chatPartner;

    @FXML private Label recipientLabel;
    @FXML private ListView<Message> messageListView;
    @FXML private TextArea messageInput;



    public void setServices(MessageService messageService, AuthService authService, User chatPartner) {
        this.messageService = messageService;
        this.authService = authService;
        this.currentUser = authService.getCurrentUser();
        this.chatPartner = chatPartner;

        messageService.addObserver(this);

        recipientLabel.setText("Chat with " + chatPartner.getEmail());
        loadMessages();
    }

    @Override
    public void update(MessageEvent event) {
        if (event.getType() == NotificationType.MESSAGE && event.getMessageStatus() == MessageStatus.NEW) {
            loadMessages();
        }
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

                            if (isMe) {
                                getStyleClass().add("my-message");
                            } else {
                                getStyleClass().add("partner-message");
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
            messageListView.setItems(FXCollections.observableArrayList(conversation));
            messageListView.scrollTo(conversation.size() - 1);
        }
    }

    @FXML
    private void handleSendMessage() {
        String text = messageInput.getText();
        if (text.isEmpty()) return;

        messageService.sendMessage(currentUser, Collections.singletonList(chatPartner), text);
        messageInput.clear();
        loadMessages();
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

        if(selectedMessage.getFrom().equals(currentUser)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Can't reply to yourself.");
            alert.show();
            return;
        }

        messageService.replyMessage(currentUser, selectedMessage, text);
        messageInput.clear();
        loadMessages();
    }
}