package org.ui.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.domain.users.User;
import org.domain.users.relationships.Friendship;
import org.service.AuthService;
import org.service.FriendshipService;
import org.service.UsersService;

import java.util.Optional;


public class FriendshipAddDialogController {

    @FXML private TextField userIdField;

    private FriendshipService service;
    private UsersService usersService;
    private AuthService authService;
    private Stage dialogStage;
    private boolean saveClicked = false;


    public void setUsersService(UsersService usersService) {
        this.usersService = usersService;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        service.setAuthService(authService);
    }

    public void setService(FriendshipService service, Stage dialogStage) {
        this.service = service;
        this.dialogStage = dialogStage;
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            try {
                Long idFriend = Long.parseLong(userIdField.getText());

                User user = usersService.findOne(idFriend);
                if(user == null){
                    showAlert("Error", "User with id " + idFriend + " not found");
                    return;
                }
                service.save(new Friendship(authService.getCurrentUser(), user));

                saveClicked = true;
                dialogStage.close();

            } catch (Exception e) {
                showAlert("Error", "Could not save duck: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();

        if(userIdField.getText().isEmpty()){ errorMessage.append("Please enter a user  id."); }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            showAlert("Invalid Fields", errorMessage.toString());
            return false;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.initOwner(dialogStage); // Make alert modal to the dialog
        alert.showAndWait();
    }
}
