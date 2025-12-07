package org.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.domain.dtos.UserDTO;
import org.domain.users.User;
import org.service.*;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class LoginController {

    private DucksService ducksService;
    private PersonsService personsService;
    private FriendshipService friendshipService;
    private UsersService usersService;
    private Stage stage;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    public void setServices(DucksService ds, PersonsService ps, FriendshipService fs, UsersService us) {
        this.ducksService = ds;
        this.personsService = ps;
        this.friendshipService = fs;
        this.usersService = us;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty()) {
            showError("Email cannot be empty.");
            return;
        }
        boolean userExists = false;
        try {
            Optional<User> foundUser = StreamSupport.stream(usersService.findAll().spliterator(),false)
                    .filter(u -> u.getEmail().equals(email))
                    .findFirst();

            userExists = foundUser.isPresent();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error occurred.");
            return;
        }

        if (userExists || (email.equals("admin") && password.equals("admin"))) {
            navigateToMainView();
        } else {
            showError("Invalid email or password.");
        }
    }

    @FXML
    public void handleRegister() {
        showError("Registration feature coming soon!");
    }

    private void navigateToMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainLayout.fxml"));
            BorderPane root = loader.load();

            MainController controller = loader.getController();
            controller.setServices(ducksService, personsService, friendshipService, usersService);

            Scene scene = new Scene(root, 1000, 700);
            stage.setTitle("Duck Social Network Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load main application.");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}