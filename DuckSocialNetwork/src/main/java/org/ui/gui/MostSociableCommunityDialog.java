package org.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.domain.dtos.guiDTOS.UserGuiDTO;

public class MostSociableCommunityDialog {

    @FXML private TableView<UserGuiDTO>  tableView;
    @FXML private TableColumn<UserGuiDTO,Long> idCol;
    @FXML private TableColumn<UserGuiDTO,String> emailCol;
    @FXML private TableColumn<UserGuiDTO,String> usernameCol;
    @FXML private TableColumn<UserGuiDTO,String> userTypeCol;

    private ObservableList<UserGuiDTO> model = FXCollections.observableArrayList();

    private Stage dialogStage;


    public void setModel(ObservableList<UserGuiDTO> model, Stage dialogStage) {
        this.model = model;
        this.dialogStage = dialogStage;
        initTable();
    }

    private void initTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("Id"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("Email"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("Username"));
        userTypeCol.setCellValueFactory(new PropertyValueFactory<>("Type"));

        tableView.setItems(model);
    }

    @FXML
    private void handleClose() {
        dialogStage.close();
    }
}
