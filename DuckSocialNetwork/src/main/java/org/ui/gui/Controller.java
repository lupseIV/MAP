package org.ui.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.domain.dtos.guiDTOS.DuckGuiDTO;
import org.service.DucksService;
import org.utils.enums.DuckTypes;

import java.util.Objects;

public class Controller {

    private DucksService ducksService;

    @FXML private TableView<DuckGuiDTO> tableView;
    @FXML private TableColumn<DuckGuiDTO, String> usernameCol;
    @FXML private TableColumn<DuckGuiDTO, String> emailCol;
    @FXML private TableColumn<DuckGuiDTO, Integer> nrOfFriendsCol;
    @FXML private TableColumn<DuckGuiDTO, DuckTypes> typeCol;
    @FXML private TableColumn<DuckGuiDTO, Double> speedCol;
    @FXML private TableColumn<DuckGuiDTO, Double> rezistanceCol;
    @FXML private TableColumn<DuckGuiDTO, String> flockNameCol;
    @FXML private ComboBox<String> comboBox;

    private final ObservableList<DuckGuiDTO> data = FXCollections.observableArrayList();
    private final ObservableList<DuckGuiDTO> masterData = FXCollections.observableArrayList();
    private final ObservableList<String> duckTypes = FXCollections.observableArrayList();

    public void setDucksService(DucksService ducksService) {
        this.ducksService = Objects.requireNonNull(ducksService);
        Platform.runLater(() -> {
            masterData.clear();
            masterData.addAll(ducksService.getGuiDucks());
            data.setAll(masterData);
            tableView.setItems(data);
        });
    }

    @FXML
    private void initialize() {
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        nrOfFriendsCol.setCellValueFactory(new PropertyValueFactory<>("nrOfFriends"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        speedCol.setCellValueFactory(new PropertyValueFactory<>("speed"));
        rezistanceCol.setCellValueFactory(new PropertyValueFactory<>("rezistance"));
        flockNameCol.setCellValueFactory(new PropertyValueFactory<>("flockName"));

        tableView.setItems(data);

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        usernameCol.setMinWidth(80);
        emailCol.setMinWidth(120);
        nrOfFriendsCol.setMinWidth(80);
        typeCol.setMinWidth(80);
        flockNameCol.setMinWidth(100);

        HBox.setHgrow(comboBox, Priority.ALWAYS);
        comboBox.setMaxWidth(Double.MAX_VALUE);



        initComboBox();
    }



    @FXML
    private void initComboBox(){
        duckTypes.clear();
        duckTypes.add("All");
        for (DuckTypes dt : DuckTypes.values()) {
            duckTypes.add(dt.name());
        }
        comboBox.setItems(duckTypes);
        comboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void filterDucks(javafx.event.ActionEvent actionEvent) {
        String selected = comboBox.getSelectionModel().getSelectedItem();
        if (selected == null || "All".equals(selected)) {
            data.setAll(masterData);
            return;
        }

        ObservableList<DuckGuiDTO> filtered = FXCollections.observableArrayList();
        for (DuckGuiDTO d : masterData) {
            if (selected.equals(d.getType())) {
                filtered.add(d);
            }
        }
        data.setAll(filtered);
    }
}
