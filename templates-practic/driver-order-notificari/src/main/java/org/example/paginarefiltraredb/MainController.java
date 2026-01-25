package org.example.paginarefiltraredb;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.paginarefiltraredb.domain.entities.Order;
import org.example.paginarefiltraredb.domain.entities.enums.OrderStatus;
import org.example.paginarefiltraredb.gui.util.crud.AddDialog;
import org.example.paginarefiltraredb.gui.util.form.FormField;
import org.example.paginarefiltraredb.service.implementations.OrderService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MainController implements ViewLoader, AddDialog<Order> {

    @FXML private VBox sideMenu;
    @FXML private StackPane contentArea;

    // Stores the logout action to be defined later
    private Runnable logoutAction;

    private OrderService orderService;

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

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

    private void resetButtonStyles() {
        for (Button b : menuButtons.values()) {
            b.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-font-size: 14px;");
        }
    }


    @FXML
    public void handleAdd(){
        // Use the AddDialog mixin's executeAdd (async saveAction expected)
        // We don't need a refresh runnable here (views will observe service events)
        executeAdd("Add New Order",
                orderService::add,
                null);
    }

    @Override
    public List<FormField> getAddFormConfig() {
        return List.of(
                FormField.text("pickupAddress", "Pickup Address", ""),
                FormField.text("destinationAddress", "Destination Address", ""),
                FormField.text("clientName", "Client Name", "")
        );
    }

    @Override
    public Order createEntity(Map<String, String> results) {
        String pickup = results.get("pickupAddress");
        String destination = results.get("destinationAddress");
        String clientName = results.get("clientName");

        // New orders start as PENDING, startDate set to now, no driver assigned yet and endDate null
        return new Order(null, OrderStatus.PENDING, LocalDateTime.now(), null, pickup, destination, clientName);
    }
}