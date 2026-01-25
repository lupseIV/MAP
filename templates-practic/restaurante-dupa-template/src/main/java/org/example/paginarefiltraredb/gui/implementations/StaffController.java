package org.example.paginarefiltraredb.gui.implementations;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.paginarefiltraredb.domain.entities.Order;
import org.example.paginarefiltraredb.domain.entities.OrderStatus;
import org.example.paginarefiltraredb.service.implementations.OrderService;
import org.example.paginarefiltraredb.service.observer.ChangeEventType;
import org.example.paginarefiltraredb.service.observer.EntityChangeEvent;
import org.example.paginarefiltraredb.service.observer.Observer;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class StaffController implements Observer<Order> {

    @FXML
    private TableView<Order> placedOrdersTable;

    @FXML
    private TableColumn<Order, Integer> placedTableIdCol;

    @FXML
    private TableColumn<Order, String> placedDateCol;

    @FXML
    private TableColumn<Order, String> placedItemsCol;

    @FXML
    private TableView<Order> preparingOrdersTable;

    @FXML
    private TableColumn<Order, Integer> preparingTableIdCol;

    @FXML
    private TableColumn<Order, String> preparingDateCol;

    @FXML
    private TableColumn<Order, String> preparingItemsCol;

    @FXML
    private Button markPreparingButton;

    @FXML
    private Button markServedButton;

    private OrderService orderService;
    private ObservableList<Order> placedOrders = FXCollections.observableArrayList();
    private ObservableList<Order> preparingOrders = FXCollections.observableArrayList();

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        // Setup Placed Orders table columns
        placedTableIdCol.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getTableId()).asObject());
        placedDateCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getDate().format(DATE_FORMAT)));
        placedItemsCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getMenuItemsAsString()));

        placedOrdersTable.setItems(placedOrders);

        // Setup Preparing Orders table columns (for bonus feature)
        if (preparingOrdersTable != null) {
            preparingTableIdCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getTableId()).asObject());
            preparingDateCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDate().format(DATE_FORMAT)));
            preparingItemsCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getMenuItemsAsString()));

            preparingOrdersTable.setItems(preparingOrders);
        }
    }

    public void setService(OrderService orderService) {
        this.orderService = orderService;
        this.orderService.addObserver(this);
        loadOrders();
    }

    private void loadOrders() {
        // Load placed orders
        List<Order> placed = orderService.getPlacedOrders();
        placedOrders.clear();
        placedOrders.addAll(placed);

        // Load preparing orders (for bonus)
        if (preparingOrdersTable != null) {
            List<Order> preparing = orderService.getPreparingOrders();
            preparingOrders.clear();
            preparingOrders.addAll(preparing);
        }
    }

    @FXML
    private void handleMarkPreparing() {
        Order selectedOrder = placedOrdersTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert("Warning", "Please select an order to mark as preparing.");
            return;
        }

        orderService.markAsPreparing(selectedOrder);
    }

    @FXML
    private void handleMarkServed() {
        if (preparingOrdersTable == null) return;

        Order selectedOrder = preparingOrdersTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert("Warning", "Please select an order to mark as served.");
            return;
        }

        orderService.markAsServed(selectedOrder);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void update(EntityChangeEvent<Order> event) {
        Platform.runLater(() -> {
            Order order = event.getData();

            if (event.getType() == ChangeEventType.ADD) {
                // New order placed
                if (order.getStatus() == OrderStatus.PLACED) {
                    placedOrders.add(order);
                    // Sort by date
                    placedOrders.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
                }
            } else if (event.getType() == ChangeEventType.UPDATE) {
                // Order status changed
                if (order.getStatus() == OrderStatus.PREPARING) {
                    // Move from placed to preparing
                    placedOrders.removeIf(o -> o.getId().equals(order.getId()));
                    if (preparingOrdersTable != null) {
                        preparingOrders.add(order);
                        preparingOrders.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
                    }
                } else if (order.getStatus() == OrderStatus.SERVED) {
                    // Remove from preparing
                    if (preparingOrdersTable != null) {
                        preparingOrders.removeIf(o -> o.getId().equals(order.getId()));
                    }
                }
            }
        });
    }
}
