package org.example.paginarefiltraredb.gui.implementations;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.paginarefiltraredb.domain.dtos.implementation.VipClientDto;
import org.example.paginarefiltraredb.domain.entities.VipClient;
import org.example.paginarefiltraredb.domain.filters.implementations.VipClientFilter;
import org.example.paginarefiltraredb.gui.AbstractPagingTableViewController;
import org.example.paginarefiltraredb.gui.util.WindowManager;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ManagerVipTableViewPagingController extends AbstractPagingTableViewController<Integer, VipClient, VipClientDto, VipClientFilter> {

    @FXML private TableColumn<VipClientDto, String> colName;
    @FXML private TableColumn<VipClientDto, Integer> colLoyalty; // ComboBox Column
    @FXML private TableColumn<VipClientDto, Void> colDone;       // CheckBox Column

    // Local storage for "Done" rows (Not stored in DB as requested)
    private final Set<Integer> completedTaskIds = new HashSet<>();

    public ManagerVipTableViewPagingController() {
        // Initialize with page 0, size 5, 0 total, and empty filter
        super(0, 5, 0, new VipClientFilter());
    }

    @Override
    protected Function<VipClient, VipClientDto> getDtoMapper() {
        return VipClientDto::new;
    }

    @Override
    public void initialize() {
        super.initialize(); // Essential to link model to table

        // 1. Standard Columns
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        // 2. Loyalty Column (ComboBox + Async DB Update)
        setupLoyaltyColumn();

        // 3. Done Column (CheckBox + Row Coloring)
        setupDoneColumn();

        // 4. Row Factory (The Coloring Logic)
        setupRowFactory();
    }

    private void setupLoyaltyColumn() {
        // Note: Using Object type for cell value factory isn't strictly necessary if we use the item from the row
        colLoyalty.setCellValueFactory(new PropertyValueFactory<>("loyaltyPoints"));

        colLoyalty.setCellFactory(col -> new TableCell<>() {
            private final ComboBox<Integer> comboBox = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5));

            {
                // Listener: Update DB when selection changes
                comboBox.setOnAction(e -> {
                    // Only react if the combo box interaction is user-driven and valid
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        VipClientDto dto = getTableRow().getItem();
                        Integer newValue = comboBox.getValue();

                        // Prevent loop if value is same
                        if (newValue != null && newValue != dto.getLoyaltyPoints()) {
                            updateLoyaltyInDatabase(dto, newValue);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    comboBox.setValue(item);
                    setGraphic(comboBox);
                }
            }
        });
    }

    private void setupDoneColumn() {
        colDone.setCellFactory(col -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                // Listener: Update Local Set & Refresh Table for Color
                checkBox.setOnAction(e -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        VipClientDto dto = getTableRow().getItem();
                        if (checkBox.isSelected()) {
                            completedTaskIds.add(dto.getId());
                        } else {
                            completedTaskIds.remove(dto.getId());
                        }
                        // Force the table to redraw rows (triggers RowFactory)
                        tableView.refresh();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    VipClientDto dto = getTableRow().getItem();
                    // Set state based on our local Set
                    checkBox.setSelected(completedTaskIds.contains(dto.getId()));
                    setGraphic(checkBox);
                }
            }
        });
    }

    private void setupRowFactory() {
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(VipClientDto item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else {
                    // PRIORITY 1: Checkbox (Green)
                    if (completedTaskIds.contains(item.getId())) {
                        setStyle("-fx-background-color: lightgreen;");
                    }
                    // PRIORITY 2: Loyalty Level (Gold/Red/White)
                    else {
                        String colorStyle = switch (item.getLoyaltyPoints()) {
                            case 5 -> "-fx-background-color: gold;";
                            case 1 -> "-fx-background-color: #ffcccc;"; // Light Red
                            default -> "";
                        };
                        setStyle(colorStyle);
                    }
                }
            }
        });
    }

    /**
     * Async Logic: Fetch Entity -> Update Field -> Save
     */
    private void updateLoyaltyInDatabase(VipClientDto dto, int newLevel) {
        // 1. Fetch the real entity (Async)
        baseService.findOne(dto.getId())
                .thenCompose(optEntity -> {
                    // 2. If found, update and save (Async)
                    if (optEntity.isPresent()) {
                        VipClient entity = optEntity.get();
                        entity.setLoyaltyPoints(newLevel);
                        return baseService.update(entity);
                    } else {
                        return CompletableFuture.completedFuture(null);
                    }
                })
                .thenAcceptAsync(updatedEntity -> {
                    // 3. UI Feedback (Back on JavaFX Thread)
                    Platform.runLater(() -> {
                        if (updatedEntity == null) {
                            WindowManager.showMessage("Success", "Loyalty updated for " + dto.getName());
                        } else {
                            WindowManager.showError("Error", "Could not update: Client not found.");
                        }
                    });
                }, Platform::runLater)
                .exceptionally(ex -> {
                    Platform.runLater(() -> WindowManager.showError("Update Failed", ex.getMessage()));
                    return null;
                });
    }

    @Override
    public void initializeTable() {

    }
}