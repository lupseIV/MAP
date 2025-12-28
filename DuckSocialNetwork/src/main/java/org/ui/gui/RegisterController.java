package org.ui.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.domain.dtos.DuckData;
import org.domain.users.User;
import org.domain.users.duck.DuckFactory;
import org.domain.users.person.Person;
import org.service.*;
import org.utils.enums.types.UserTypes;

import java.io.IOException;
import java.util.List;

public class RegisterController {

    private DuckFactory duckFactory = new DuckFactory();
    private DucksService ducksService;
    private PersonsService personsService;
    private FriendshipService friendshipService;
    private UsersService usersService;
    private AuthService authService;
    private MessageService messageService;
    private NotificationService notificationService;
    private RaceEventService raceEventService;
    private Stage stage;

    private UserTypes userType;
    @FXML private GridPane userTypeFields;

    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private TextField emailField;
    @FXML private TextField confirmPasswordField;

    @FXML private ComboBox<UserTypes> userTypeComboBox;

    private FieldsController currentSubController;

    @FXML
    private void initialize() {
        userTypeComboBox.setItems(
                FXCollections.observableArrayList(

                                UserTypes.values()));
    }

    public void setServices(DucksService ds, PersonsService ps, FriendshipService fs, UsersService us,
                            AuthService as, MessageService ms, NotificationService ns, RaceEventService res) {
        this.raceEventService = res;
        this.ducksService = ds;
        this.personsService = ps;
        this.friendshipService = fs;
        this.usersService = us;
        this.authService = as;
        this.messageService = ms;
        this.notificationService = ns;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    @FXML
    public void handleRegister() {
        if(userTypeComboBox.getSelectionModel().getSelectedIndex() == -1) {
            showAlert("Error", "Please select a user type!");
            return;
        }
        if(usernameField.getText().isEmpty() || passwordField.getText().isEmpty()){
            showAlert("Error", "Please fill all the fields.");
            return;
        }
        if(!passwordField.getText().equals(confirmPasswordField.getText())){
            showAlert("Error", "Passwords do not match.");
            return;
        }

        try {
            var user = getUserFromType();
            authService.register(user);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error",e.getMessage());
            return;
        }

        if (authService.isLoggedIn()) {
            navigateToMainView();
        } else {
            showAlert("Failed","Registration failed.");
        }

    }

    @FXML
    private void handleCancel(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
            VBox root = loader.load();

            LoginController controller = loader.getController();
            controller.setServices(ducksService, personsService, friendshipService, usersService,
                    authService, messageService, notificationService, raceEventService);


            Scene scene = new Scene(root, 1000, 700);
            controller.setStage(stage);
            stage.setTitle("Duck Social Network Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error","Failed to load main application.");
        }
    }

    @FXML
    private void handleUserTypeSelection() {
        UserTypes selectedType = userTypeComboBox.getSelectionModel().getSelectedItem();
        if (selectedType == null) return;

        if (selectedType == UserTypes.PERSON) {
            loadFieldsView("PersonFieldsView.fxml");
        } else if (selectedType == UserTypes.DUCK) {
            loadFieldsView("DuckFieldsView.fxml");
        }
    }

    private void loadFieldsView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Node node = loader.load();

            userTypeFields.getChildren().clear();
            userTypeFields.getChildren().add(node);

            this.currentSubController = loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load fields for selection.");
        }
    }

    private void navigateToMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainLayout.fxml"));
            BorderPane root = loader.load();


            MainController controller = loader.getController();
            controller.setServices(ducksService, personsService, friendshipService,
                    usersService, authService, messageService, notificationService, raceEventService);

            Scene scene = new Scene(root, 1000, 700);
            stage.setTitle("Duck Social Network Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error","Failed to load main application.");
        }
    }

    private User getUserFromType() {
        UserTypes type = userTypeComboBox.getSelectionModel().getSelectedItem();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String errs = currentSubController.isValid();
        if(!errs.isEmpty()) {
            showAlert("Error", errs);
        }
        if (type == UserTypes.PERSON) {
            if (currentSubController instanceof PersonFieldsController) {
                PersonFieldsController pc = (PersonFieldsController) currentSubController;

                return new Person(
                        username,
                        password,
                        email,
                        pc.getFirstName(),
                        pc.getLastName(),
                        pc.getOccupation(),
                        pc.getBirthDate(),
                        pc.getEmphatyLevel()
                );
            }
        } else if (type == UserTypes.DUCK) {
            if (currentSubController instanceof DuckFieldsController dc) {

                DuckData duckData = new DuckData(List.of(
                        username,
                        password,
                        email,
                        dc.getSpeed().toString(),
                        dc.getResistance().toString()
                ));

                return duckFactory.create(dc.getDuckType(), duckData);
            }
        }

        throw new IllegalArgumentException("Invalid user type or fields not loaded.");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
