package org.ui.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.domain.dtos.PersonData;
import org.domain.users.UserFactory;
import org.domain.users.person.Person;
import org.service.PersonsService;
import org.utils.enums.types.PersonTypes;

import java.time.LocalDate;
import java.util.List;

public class PersonAddDialogController {
    @FXML
    private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField occupationField;
    @FXML private DatePicker dobPicker;
    @FXML private TextField emphatyLevelField;

    private PersonsService service;
    private final UserFactory userFactory = new UserFactory();
    private Stage dialogStage;
    private boolean saveClicked = false;

    @FXML
    public void initialize() {
    }

    public void setService(PersonsService service, Stage dialogStage) {
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
                String username = usernameField.getText();
                String password = passwordField.getText();
                String email = emailField.getText();
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String occupation = occupationField.getText();
                LocalDate dob = dobPicker.getValue();
                String emphatyLevel = emphatyLevelField.getText();

                PersonData personData = new PersonData(
                        List.of(username, password, email,firstName,lastName,occupation,dob.toString(),emphatyLevel)
                );


                service.save((Person) userFactory.createUser(PersonTypes.DEFAULT, personData));

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

        if (usernameField.getText().isEmpty()) errorMessage.append("Username is required!\n");
        if (passwordField.getText().isEmpty()) errorMessage.append("Password is required!\n");
        if (emailField.getText().isEmpty()) errorMessage.append("Email is required!\n");
        if (firstNameField.getText().isEmpty()) errorMessage.append("First Name is required!\n");

        if (occupationField.getText().isEmpty()) errorMessage.append("Occupation is required!\n");

        if (dobPicker.getValue() == null) {
            errorMessage.append("Date of Birth is required!\n");
        } else if (dobPicker.getValue().isAfter(LocalDate.now())) {
            errorMessage.append("Date of Birth cannot be in the future!\n");
        }

        try {
            Double.parseDouble(emphatyLevelField.getText());
        } catch (NumberFormatException e) {
            errorMessage.append("Empathy level must be a valid number!\n");
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
