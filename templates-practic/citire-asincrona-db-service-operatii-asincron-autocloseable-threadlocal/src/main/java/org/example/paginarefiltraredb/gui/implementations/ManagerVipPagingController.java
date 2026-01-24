package org.example.paginarefiltraredb.gui.implementations;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.example.paginarefiltraredb.domain.dtos.implementation.VipClientDto;
import org.example.paginarefiltraredb.domain.entities.VipClient;
import org.example.paginarefiltraredb.domain.filters.implementations.VipClientFilter;
import org.example.paginarefiltraredb.gui.AbstractPagingListViewController;

public class ManagerVipPagingController extends AbstractPagingListViewController<Integer, VipClient, VipClientDto, VipClientFilter> {

    public ManagerVipPagingController() {
        super(0, 2, 0, new VipClientFilter());
    }

    @Override
    protected java.util.function.Function<VipClient, VipClientDto> getDtoMapper() {
        return VipClientDto::new;
    }

    @Override
    protected void setupListCellFactory() {
        listView.setCellFactory(param -> new ListCell<>() {
            private final CheckBox chkFinished = new CheckBox("Done");
            private final ComboBox<Integer> cmbLoyalty = new ComboBox<>();
            private final Label lblName = new Label();
            private final Region spacer = new Region();
            private final HBox layout = new HBox(10, chkFinished, lblName, spacer, cmbLoyalty);

            {
                HBox.setHgrow(spacer, Priority.ALWAYS);
                cmbLoyalty.getItems().addAll(1, 2, 3, 4, 5);

                cmbLoyalty.setOnAction(e -> {
                    if (getItem() != null) {
                        getItem().setLoyaltyPoints(cmbLoyalty.getValue());
                        updateRowColor(this, getItem(), chkFinished.isSelected());
                    }
                });

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
                    chkFinished.setSelected(false);
                    updateRowColor(this, item, false);
                    setGraphic(layout);
                }
            }
        });
    }

    private void updateRowColor(ListCell<?> cell, VipClientDto item, boolean isFinished) {
        if (isFinished) {
            cell.setStyle("-fx-background-color: lightgreen;");
        } else {
            String color = switch (item.getLoyaltyPoints()) {
                case 1 -> "#ffcccc";
                case 5 -> "gold";
                default -> "white";
            };
            cell.setStyle("-fx-background-color: " + color + ";");
        }
    }
}