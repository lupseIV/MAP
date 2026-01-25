package org.example.paginarefiltraredb.gui.implementations;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.example.paginarefiltraredb.domain.dtos.implementation.OrderDTO;
import org.example.paginarefiltraredb.domain.entities.Driver;
import org.example.paginarefiltraredb.domain.entities.Order;
import org.example.paginarefiltraredb.domain.entities.enums.OrderStatus;
import org.example.paginarefiltraredb.domain.filters.SqlFilter;
import org.example.paginarefiltraredb.domain.filters.implementations.OrderFilter;
import org.example.paginarefiltraredb.gui.AbstractListViewController;
import org.example.paginarefiltraredb.gui.AbstractTableViewController;
import org.example.paginarefiltraredb.gui.util.WindowManager;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DriverController extends AbstractTableViewController<Integer, Order, OrderDTO> {

    private Integer currentDriver;
    private final OrderFilter filter = new OrderFilter();

    // Track finished orders so the UI can reflect state immediately when needed
    private final Set<Integer> completedTaskIds = new HashSet<>();

    @FXML private ComboBox<String> typeCombo;

    @FXML private TableColumn<OrderDTO, Integer> idCol;
    @FXML private TableColumn<OrderDTO, String> pickUpCol;
    @FXML private TableColumn<OrderDTO, String> destCol;
    @FXML private TableColumn<OrderDTO, Void> colDone;


    public void setCurrentDriver(Integer currentDriver) {
        this.currentDriver = currentDriver;
        filter.setDriverId(currentDriver);
        filter.setStatus("IN_PROGRESS"); // show only active (in progress) orders for driver
        loadData();
    }

    @Override
    protected Function<Order, OrderDTO> getDtoMapper() {
        return OrderDTO::new;
    }

    @FXML
    public void initialize() {
        super.initialize();

        // Populate filter type combo with enum names
        typeCombo.getItems().clear();
        typeCombo.getItems().addAll(Arrays.stream(OrderStatus.values()).map(Enum::name).collect(Collectors.toList()));
        typeCombo.getItems().add(0, "No selection");
        typeCombo.getSelectionModel().select("IN_PROGRESS");

        typeCombo.valueProperty()
                .addListener((obs, oldVal, newVal) -> { handleComboBoxFilter(); });

        setupDoneColumn();
        setupRowFactory();

        // Do not call loadData here because setCurrentDriver may not have been called yet. If it has been called it invoked loadData.
    }

    private void setupDoneColumn() {
        colDone.setCellFactory(col -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                // Listener: Update Order and refresh table
                checkBox.setOnAction(e -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        OrderDTO dto = getTableRow().getItem();
                        // Load the full entity, update status and endDate, save
                        baseService.findOne(dto.getId()).thenAccept(optOrder -> {
                            optOrder.ifPresent(order -> {
                                order.setStatus(OrderStatus.FINISHED);
                                order.setEndDate(LocalDateTime.now());

                                baseService.update(order).thenAccept(updated -> {
                                    // Reload the active list for this driver
                                    Platform.runLater(this::getTableViewRefresh);
                                }).exceptionally(ex -> {
                                    Platform.runLater(() -> WindowManager.showError("Error", "Could not update order: " + ex.getMessage()));
                                    return null;
                                });
                            });
                        }).exceptionally(ex -> {
                            Platform.runLater(() -> WindowManager.showError("Error", "Could not load order: " + ex.getMessage()));
                            return null;
                        });
                    }
                });
            }

            private void getTableViewRefresh() {
                // Clear local completed ids (we'll repopulate on load) and reload data
                DriverController.this.completedTaskIds.clear();
                loadData();
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    OrderDTO dto = getTableRow().getItem();
                    // Set state based on our local Set
                    checkBox.setSelected(DriverController.this.completedTaskIds.contains(dto.getId()));
                    setGraphic(checkBox);
                }
            }
        });
    }

    private void setupRowFactory() {
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(OrderDTO item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else {
                    if (completedTaskIds.contains(item.getId())) {
                        setStyle("-fx-background-color: lightgreen;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }


    @Override
    public void loadData() {
        if (baseService == null) return;

        CompletableFuture<Iterable<Order>> allEntitiesFuture = baseService.findAll(filter);

        allEntitiesFuture.thenCompose(iterable -> {
            // Populate completedTaskIds from the actual entities so the UI reflects persisted state if needed
            completedTaskIds.clear();
            for (Order o : iterable) {
                if (o.getStatus() == OrderStatus.FINISHED && o.getId() != null) {
                    completedTaskIds.add(o.getId());
                }
            }
            // Convert to DTOs using existing helper (wrap iterable into completed future)
            return baseService.convertIterableToDto(CompletableFuture.completedFuture(iterable), getDtoMapper());
        }).thenAccept(allDtos -> {
            Platform.runLater(() -> model.setAll(allDtos));
        }).exceptionally((e) -> {
            Platform.runLater(() -> WindowManager.showError("Error", "Could not load data: " + e.getMessage()));
            e.printStackTrace();
            return null;
        });
    }

    @FXML
    public void handleComboBoxFilter() {
        String type = typeCombo.getValue();
        if("No selection".equals(type)){
            filter.setStatus(null);
        } else {
            filter.setStatus(type);
        }
        loadData();
    }


    private void updateRowColor(ListCell<?> cell, boolean isFinished) {
        if (isFinished) {
            // "if it is checked the whole row is colored"
            cell.setStyle("-fx-background-color: lightgreen;");
        } else {
            cell.setStyle(null);
        }
    }

    @Override
    public void update(org.example.paginarefiltraredb.service.observer.EntityChangeEvent<Order> event) {
        // Call default behavior to reload only for non-add events
        if (event.getType() != org.example.paginarefiltraredb.service.observer.ChangeEventType.ADD) {
            loadData();
            return;
        }

        Order notified = event.getData();
        if (notified == null) return;

        // If the notification is not targeted to this driver, ignore
        if (currentDriver == null || !currentDriver.equals(notified.getDriverId())) return;

        // If driver already has an active IN_PROGRESS, ignore (Service should exclude such drivers but double-check)
        OrderFilter activeFilter = new OrderFilter();
        activeFilter.setDriverId(currentDriver);
        activeFilter.setStatus("IN_PROGRESS");
        Iterable<Order> active = baseService.findAll(activeFilter).join();
        if (active.iterator().hasNext()) return;

        // Show dialog on FX thread with 5s timeout
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("New order");
            alert.setHeaderText(null);
            alert.setContentText("New order: " + notified.getPickUpAddress() + " -> " + notified.getDestinationAddress());

            ButtonType accept = new ButtonType("Accept");
            ButtonType decline = new ButtonType("Decline");
            alert.getButtonTypes().setAll(accept, decline);

            // Show non-blocking and wait up to 5s for result
            CompletableFuture<java.util.Optional<ButtonType>> future = CompletableFuture.supplyAsync(() -> {
                try {
                    // Sleep on worker thread to allow FX thread to show dialog
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return java.util.Optional.empty();
            });

            // Show dialog and handle accept immediately if clicked
            alert.show();
            alert.resultProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal == accept) {
                    // Driver accepted - update order
                    baseService.findOne(notified.getId()).thenAccept(opt -> {
                        opt.ifPresent(order -> {
                            order.setStatus(OrderStatus.IN_PROGRESS);
                            order.setDriverId(currentDriver);
                            order.setStartDate(java.time.LocalDateTime.now());
                            baseService.update(order).thenAccept(u -> {
                                Platform.runLater(() -> {
                                    alert.close();
                                    loadData();
                                });
                            });
                        });
                    });
                }
            });

            // After 5 seconds, if dialog still showing, close it (this counts as timeout)
            CompletableFuture.runAsync(() -> {
                try { Thread.sleep(5000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                Platform.runLater(() -> {
                    if (alert.isShowing()) {
                        alert.close();
                    }
                });
            });
        });
    }
}
