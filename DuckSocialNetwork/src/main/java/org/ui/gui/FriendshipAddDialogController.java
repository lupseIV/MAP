package org.ui.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.domain.users.User;
import org.domain.users.relationships.Friendship;
import org.service.FriendshipService;
import org.service.UsersService;

public class FriendshipAddDialogController {

    @FXML private TextField user1IdField;
    @FXML private TextField user2IdField;

    private FriendshipService service;
    private UsersService usersService;
    private Stage dialogStage;
    private boolean saveClicked = false;

    @FXML
    public void initialize() {
    }

    public void setUsersService(UsersService usersService){
        this.usersService = usersService;
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
                Long idUser1 = Long.parseLong(user1IdField.getText());
                Long idUser2 = Long.parseLong(user2IdField.getText());

                User user1 = usersService.findOne(idUser1);
                User user2 = usersService.findOne(idUser2);

                service.save(new Friendship(user1,user2));

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

        if(user1IdField.getText().isEmpty()){ errorMessage.append("Please enter a user 1 id."); }
        if(user2IdField.getText().isEmpty()){ errorMessage.append("Please enter a user 2 id."); }

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
