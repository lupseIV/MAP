package org.ui.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.domain.users.User;
import org.domain.users.duck.Duck;
import org.domain.users.person.Person;
import org.service.UsersService;

public class UserEditDialogController {

    @FXML private VBox personContainer;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField jobField;
    @FXML private TextField empathyField;

    @FXML private VBox duckContainer;
    @FXML private TextField speedField;
    @FXML private TextField resistanceField;

    @FXML private TextArea descriptionField;

    private UsersService usersService;
    private User user;
    private Stage stage;

    public void setService(UsersService usersService, User user, Stage stage) {
        this.usersService = usersService;
        this.user = user;
        this.stage = stage;
        populateFields();
    }

    private void populateFields() {

        descriptionField.setText(user.getDescription());

        if (user instanceof Person) {
            Person p = (Person) user;
            personContainer.setVisible(true);
            personContainer.setManaged(true);
            firstNameField.setText(p.getFirstName());
            lastNameField.setText(p.getLastName());
            jobField.setText(p.getOccupation());
            empathyField.setText(String.valueOf(p.getEmpathyLevel()));
        } else if (user instanceof Duck) {
            Duck d = (Duck) user;
            duckContainer.setVisible(true);
            duckContainer.setManaged(true);
            speedField.setText(String.valueOf(d.getSpeed()));
            resistanceField.setText(String.valueOf(d.getRezistance()));
        }
    }

    @FXML
    private void handleSave() {
        try {
            user.setDescription(descriptionField.getText());

            if (user instanceof Person) {
                Person p = (Person) user;
                p.setFirstName(firstNameField.getText());
                p.setLastName(lastNameField.getText());
                p.setOccupation(jobField.getText());
                p.setEmpathyLevel(Double.parseDouble(empathyField.getText()));
                usersService.update(p);
            } else if (user instanceof Duck) {
                Duck d = (Duck) user;
                d.setSpeed(Double.parseDouble(speedField.getText()));
                d.setRezistance(Double.parseDouble(resistanceField.getText()));
                usersService.update(d);
            }
            stage.close();
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }
}