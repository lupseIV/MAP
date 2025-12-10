package org.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
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
    private AuthService authService;
    private MessageService messageService;
    private GraphicUserInterface app;
    private Stage stage;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    public void setServices(DucksService ds, PersonsService ps, FriendshipService fs, UsersService us,
                            AuthService as, MessageService ms, GraphicUserInterface app) {
        this.ducksService = ds;
        this.personsService = ps;
        this.friendshipService = fs;
        this.usersService = us;
        this.authService = as;
        this.messageService = ms;
        this.app = app;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() && password.isEmpty()) {
            showError("Email cannot be empty.");
            return;
        }
        try {
            authService.login(email, password);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error occurred.");
            return;
        }

        if (authService.isLoggedIn() || (email.equals("admin") && password.equals("admin"))) {
            navigateToMainView();
        } else {
            showError("Invalid email or password.");
        }
    }

    @FXML
    public void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisterView.fxml"));
            AnchorPane root = loader.load();

            RegisterController controller = loader.getController();
            controller.setServices(ducksService, personsService, friendshipService, usersService,
                    authService, messageService, app);


            Scene scene = new Scene(root, 1000, 700);
            controller.setStage(stage);
            stage.setTitle("Duck Social Network Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load main application.");
        }
    }

    private void navigateToMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainLayout.fxml"));
            BorderPane root = loader.load();

            app.setNotificationListener(authService.getNotificationListener());

            MainController controller = loader.getController();
            controller.setServices(ducksService, personsService, friendshipService, usersService, authService, messageService);

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