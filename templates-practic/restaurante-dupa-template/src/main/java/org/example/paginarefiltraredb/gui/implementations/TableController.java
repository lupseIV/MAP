package org.example.paginarefiltraredb.gui.implementations;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;
import org.example.paginarefiltraredb.domain.entities.MenuItem;
import org.example.paginarefiltraredb.domain.entities.Order;
import org.example.paginarefiltraredb.service.implementations.MenuItemService;
import org.example.paginarefiltraredb.service.implementations.OrderService;
import org.example.paginarefiltraredb.service.observer.EntityChangeEvent;
import org.example.paginarefiltraredb.service.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TableController implements Observer<Order> {

    @FXML
    private VBox menuContainer;

    @FXML
    private Button placeOrderButton;

    @FXML
    private Label notificationLabel;

    private MenuItemService menuItemService;
    private OrderService orderService;
    private Integer tableId;

    // Track selected items with checkboxes
    private List<CheckBox> menuCheckBoxes = new ArrayList<>();
    private List<MenuItem> allMenuItems = new ArrayList<>();

    public void setServices(MenuItemService menuItemService, OrderService orderService) {
        this.menuItemService = menuItemService;
        this.orderService = orderService;
        this.orderService.addObserver(this);
        loadMenu();
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    private void loadMenu() {
        menuContainer.getChildren().clear();
        menuCheckBoxes.clear();
        allMenuItems.clear();

        Map<String, List<MenuItem>> groupedMenu = menuItemService.getMenuGroupedByCategory();

        for (Map.Entry<String, List<MenuItem>> entry : groupedMenu.entrySet()) {
            String category = entry.getKey();
            List<MenuItem> items = entry.getValue();

            // Category header
            Label categoryLabel = new Label(category);
            categoryLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 0 5 0;");
            menuContainer.getChildren().add(categoryLabel);

            // Table for this category
            TableView<MenuItem> categoryTable = new TableView<>();
            categoryTable.setEditable(true);
            categoryTable.setPrefHeight(150);

            // Select column with checkbox
            TableColumn<MenuItem, Boolean> selectCol = new TableColumn<>("Select");
            selectCol.setCellFactory(col -> {
                CheckBoxTableCell<MenuItem, Boolean> cell = new CheckBoxTableCell<>();
                CheckBox checkBox = new CheckBox();
                cell.setGraphic(checkBox);

                checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                    MenuItem item = cell.getTableRow().getItem();
                    if (item != null) {
                        // Track selection state
                        if (isSelected && !allMenuItems.contains(item)) {
                            // Mark as selected
                        }
                    }
                });

                return cell;
            });
            selectCol.setPrefWidth(60);

            // Item name column
            TableColumn<MenuItem, String> itemCol = new TableColumn<>("Item");
            itemCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getItem()));
            itemCol.setPrefWidth(200);

            // Price column
            TableColumn<MenuItem, String> priceCol = new TableColumn<>("Price");
            priceCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPrice() + " " + data.getValue().getCurrency()));
            priceCol.setPrefWidth(100);

            categoryTable.getColumns().addAll(selectCol, itemCol, priceCol);

            // Add items to table
            ObservableList<MenuItem> tableItems = FXCollections.observableArrayList(items);
            categoryTable.setItems(tableItems);

            // Store items for later selection
            allMenuItems.addAll(items);

            // Add selection handling
            categoryTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            menuContainer.getChildren().add(categoryTable);
            VBox.setMargin(categoryTable, new Insets(0, 0, 15, 0));
        }
    }

    @FXML
    private void handlePlaceOrder() {
        // Collect selected items from all tables
        List<MenuItem> selectedItems = new ArrayList<>();

        for (javafx.scene.Node node : menuContainer.getChildren()) {
            if (node instanceof TableView) {
                @SuppressWarnings("unchecked")
                TableView<MenuItem> table = (TableView<MenuItem>) node;
                selectedItems.addAll(table.getSelectionModel().getSelectedItems());
            }
        }

        if (selectedItems.isEmpty()) {
            showAlert("Validation Error", "Please select at least one product before placing an order!");
            return;
        }

        try {
            Order order = orderService.placeOrder(tableId, selectedItems);
            if (order == null) {
                showAlert("Success", "Order placed successfully!");
                clearSelections();
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to place order: " + e.getMessage());
        }
    }

    private void clearSelections() {
        for (javafx.scene.Node node : menuContainer.getChildren()) {
            if (node instanceof TableView) {
                @SuppressWarnings("unchecked")
                TableView<MenuItem> table = (TableView<MenuItem>) node;
                table.getSelectionModel().clearSelection();
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showNotification(String message) {
        if (notificationLabel != null) {
            notificationLabel.setText(message);
            notificationLabel.setVisible(true);

            // Hide after 5 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    javafx.application.Platform.runLater(() -> {
                        notificationLabel.setVisible(false);
                        notificationLabel.setText("");
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void handleAdd(){

    }


    @Override
    public void update(EntityChangeEvent<Order> event) {
        Order order = event.getData();

        // Check if this order is for this table
        if (order != null && order.getTableId().equals(tableId)) {
            javafx.application.Platform.runLater(() -> {
                switch (order.getStatus()) {
                    case PREPARING:
                        showNotification("Your order is being prepared");
                        break;
                    case SERVED:
                        showNotification("Your order has been delivered to your table");
                        break;
                    default:
                        break;
                }
            });
        }
    }
}
