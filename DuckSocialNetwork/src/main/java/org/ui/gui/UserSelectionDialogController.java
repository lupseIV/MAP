package org.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.domain.users.User;
import org.service.AuthService;
import org.service.UsersService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserSelectionDialogController {
    
    @FXML
    private ListView<User> userListView;
    
    private Stage dialogStage;
    private User selectedUser;
    private boolean okClicked = false;
    private UsersService usersService;
    private AuthService authService;
    
    @FXML
    private void initialize() {
        userListView.setCellFactory(lv -> new javafx.scene.control.ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getUsername() + " (" + user.getEmail() + ")");
                }
            }
        });
    }
    
    public void setServices(UsersService usersService, AuthService authService, Stage dialogStage) {
        this.usersService = usersService;
        this.authService = authService;
        this.dialogStage = dialogStage;
        loadUsers();
    }
    
    private void loadUsers() {
        List<User> users = StreamSupport.stream(usersService.findAll().spliterator(), false)
                .filter(u -> !u.equals(authService.getCurrentUser()))
                .collect(Collectors.toList());
        
        ObservableList<User> observableUsers = FXCollections.observableArrayList(users);
        userListView.setItems(observableUsers);
    }
    
    @FXML
    private void handleOk() {
        selectedUser = userListView.getSelectionModel().getSelectedItem();
        
        if (selectedUser == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No User Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a user to chat with.");
            alert.showAndWait();
            return;
        }
        
        okClicked = true;
        dialogStage.close();
    }
    
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    
    public boolean isOkClicked() {
        return okClicked;
    }
    
    public User getSelectedUser() {
        return selectedUser;
    }
}
