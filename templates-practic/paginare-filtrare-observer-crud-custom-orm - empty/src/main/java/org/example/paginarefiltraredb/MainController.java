package org.example.paginarefiltraredb;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MainController implements ViewLoader {

    @FXML private VBox sideMenu;
    @FXML private StackPane contentArea;

    // Stores the logout action to be defined later
    private Runnable logoutAction;

    // Keep track of active buttons to manage styling (optional)
    private final Map<String, Button> menuButtons = new HashMap<>();

    /**
     * DYNAMICALLY adds a menu option.
     * * @param title        The text on the button (e.g., "Users", "Settings")
     * @param fxmlPath     The path to the FXML view (e.g., "UsersView.fxml")
     * @param configurator A lambda to configure the controller of that view (inject services)
     */
    public void addMenuOption(String title, String fxmlPath, ControllerConfigurator configurator) {
        Button btn = new Button(title);

        // 1. Generic Styling (Dark Theme)
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-font-size: 14px; -fx-cursor: hand;");
        btn.setPadding(new javafx.geometry.Insets(10, 0, 10, 20));

        // 2. Hover Effects
        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains("34495e"))
                btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-font-size: 14px;");
        });
        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().contains("2980b9"))
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-font-size: 14px;");
        });

        // 3. Click Action
        btn.setOnAction(e -> {
            // Highlight active button
            resetButtonStyles();
            btn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-font-size: 14px; -fx-font-weight: bold;");

            // Load view using your interface
            loadView(fxmlPath, configurator, contentArea);
        });

        menuButtons.put(title, btn);
        sideMenu.getChildren().add(btn);
    }

    public void setLogoutAction(Runnable action) {
        this.logoutAction = action;
    }

    @FXML
    public void handleLogout() {
        if (logoutAction != null) {
            logoutAction.run();
        }
    }

    private void resetButtonStyles() {
        for (Button b : menuButtons.values()) {
            b.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-font-size: 14px;");
        }
    }
}