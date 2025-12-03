package org.ui.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.domain.dtos.DuckData;
import org.domain.users.UserFactory;
import org.domain.users.duck.Duck;
import org.service.DucksService;
import org.utils.enums.DuckTypes;

import java.util.List;

public class DuckAddDialogController {

    @FXML private TextField nameField;
    @FXML private TextField passwordField;
    @FXML private TextField emailField;
    @FXML private ComboBox<DuckTypes> typeComboBox;
    @FXML private TextField speedField;
    @FXML private TextField rezistanceField;

    private DucksService service;
    private final UserFactory userFactory = new UserFactory();
    private Stage dialogStage;
    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        typeComboBox.getItems().setAll(DuckTypes.values());
    }

    public void setService(DucksService service, Stage dialogStage) {
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
                String username = nameField.getText();
                String password = passwordField.getText();
                String email = emailField.getText();
                DuckTypes type = typeComboBox.getValue();
                double speed = Double.parseDouble(speedField.getText());
                double rezistance = Double.parseDouble(rezistanceField.getText());

                DuckData duckData = new DuckData(
                        List.of(username, password, email, String.valueOf(speed), String.valueOf(rezistance))
                );


                service.save((Duck)userFactory.createUser(type, duckData));

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
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errorMessage += "No valid name!\n";
        }
        if (typeComboBox.getValue() == null) {
            errorMessage += "Please select a duck type!\n";
        }
//        if (colorsField.getText() == null || colorsField.getText().trim().isEmpty()) {
//            errorMessage += "No valid color count!\n";
//        } else {
//            try {
//                int c = Integer.parseInt(colorsField.getText());
//                if (c < 0) errorMessage += "Colors must be positive!\n";
//            } catch (NumberFormatException e) {
//                errorMessage += "Colors must be a number (integer)!\n";
//            }
//        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            showAlert("Invalid Fields", errorMessage);
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