package org.example.paginarefiltraredb.gui.implementations;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.example.paginarefiltraredb.domain.dtos.implementation.VipClientDto;
import org.example.paginarefiltraredb.domain.entities.VipClient;
import org.example.paginarefiltraredb.gui.AbstractListViewController;

public class ManagerVipListController extends AbstractListViewController<Integer, VipClient, VipClientDto> {

    @Override
    protected java.util.function.Function<VipClient, VipClientDto> getDtoMapper() {
        return VipClientDto::new;
    }

    @Override
    protected void setupListCellFactory() {
        listView.setCellFactory(param -> new ListCell<>() {

            // Create controls ONCE
            private final CheckBox chkFinished = new CheckBox("Done");
            private final ComboBox<Integer> cmbLoyalty = new ComboBox<>();
            private final Label lblName = new Label();
            private final Region spacer = new Region();
            private final HBox layout = new HBox(10, chkFinished, lblName, spacer, cmbLoyalty);

            {
                HBox.setHgrow(spacer, Priority.ALWAYS); // Push combos to the right
                cmbLoyalty.getItems().addAll(1, 2, 3, 4, 5);

                // Listener: Update style on ComboBox change
                cmbLoyalty.setOnAction(e -> {
                    if (getItem() != null) {
                        // In real app, call service.updateLoyalty() here
                        getItem().setLoyaltyPoints(cmbLoyalty.getValue());
                        updateRowColor(this, getItem(), chkFinished.isSelected());
                    }
                });

                // Listener: Update style on CheckBox change
                chkFinished.selectedProperty().addListener((obs, old, isSelected) -> {
                    updateRowColor(this, getItem(), isSelected);
                });
            }

            @Override
            protected void updateItem(VipClientDto item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setStyle("");
                } else {
                    lblName.setText(item.getName());
                    cmbLoyalty.setValue(item.getLoyaltyPoints());
                    chkFinished.setSelected(false); // Default unchecked as per req

                    updateRowColor(this, item, false);
                    setGraphic(layout);
                }
            }
        });
    }

    private void updateRowColor(ListCell<?> cell, VipClientDto item, boolean isFinished) {
        if (isFinished) {
            // "if it is checked the whole row is colored"
            cell.setStyle("-fx-background-color: lightgreen;");
        } else {
            // "change based on loyalty color"
            String color = switch (item.getLoyaltyPoints()) {
                case 1 -> "#ffcccc"; // Red
                case 5 -> "gold";    // Gold
                default -> "white";
            };
            cell.setStyle("-fx-background-color: " + color + ";");
        }
    }
}