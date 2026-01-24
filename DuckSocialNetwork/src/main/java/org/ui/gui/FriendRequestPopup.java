package org.ui.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.domain.users.User;
import org.domain.users.relationships.Friendship;
import org.service.AuthService;
import org.service.FriendshipService;

public class FriendRequestPopup {
    private User from;
    private Friendship friendship;
    private FriendshipService service;
    private AuthService authService;
    private boolean clicked = false;

    public boolean isClicked() {
        return clicked;
    }

    @FXML
    private Label fromLabel;
    private Stage dialogStage;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        service.setAuthService(authService);
    }

    public void setService(Stage dialogStage, User from, Friendship friendship, FriendshipService service) {
        this.dialogStage = dialogStage;
        this.from = from;
        this.friendship = friendship;
        this.service = service;
        initLabel();
    }

    public void initLabel(){
        fromLabel.setText("Friend request from " + from.getUsername());
    }


    @FXML
    private void acceptFriendRequest(){
        clicked = true;
        service.acceptFriendship(friendship);
        dialogStage.close();
    }

    @FXML
    private void rejectFriendRequest(){
        clicked = true;
        service.rejectFriendship(friendship);
        dialogStage.close();
    }

}
