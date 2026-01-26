package org.example.paginarefiltraredb.gui.implementations;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.paginarefiltraredb.domain.entities.User;
import org.example.paginarefiltraredb.service.UserService;

import java.util.Optional;
import java.util.function.Consumer;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserService userService;
    private Consumer<User> onLoginSuccess;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setOnLoginSuccess(Consumer<User> onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        if (userService == null) {
            showError("Service not initialized");
            return;
        }

        Optional<User> userOpt = userService.authenticate(username, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            hideError();
            if (onLoginSuccess != null) {
                onLoginSuccess.accept(user);
            }
        } else {
            showError("Invalid username or password");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
    }
}
