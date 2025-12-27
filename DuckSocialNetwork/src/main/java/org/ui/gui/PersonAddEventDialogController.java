package org.ui.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.domain.events.RaceEvent;
import org.service.RaceEventService;

public class PersonAddEventDialogController {
    @FXML
    private TextField nameField;

    private Stage dialogStage;
    private RaceEventService service;
    private boolean saveClicked = false;

    @FXML
    private void initialize() {
    }

    public void setServices(RaceEventService service, Stage dialogStage) {
        this.service = service;
        this.dialogStage = dialogStage;
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            String name = nameField.getText();

            RaceEvent newEvent = new RaceEvent(name);

            service.save(newEvent);

            saveClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errorMessage.append("No valid event name!\n");
        }


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
