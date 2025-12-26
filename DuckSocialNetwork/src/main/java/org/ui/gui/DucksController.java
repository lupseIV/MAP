package org.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.domain.dtos.filters.DuckGUIFilter;
import org.domain.dtos.guiDTOS.DuckGuiDTO;
import org.domain.users.duck.Duck;
import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;
import org.service.DucksService;
import org.service.UsersService;
import org.utils.enums.DuckTypes;

import java.io.IOException;
import java.util.Optional;

public class DucksController extends AbstractPagingTableViewController<DuckGuiDTO, DuckGUIFilter>{

    @FXML private TableView<DuckGuiDTO> tableView;
    @FXML private TableColumn<DuckGuiDTO, Long> idCol;
    @FXML private TableColumn<DuckGuiDTO, String> usernameCol;
    @FXML private TableColumn<DuckGuiDTO, String> emailCol;
    @FXML private TableColumn<DuckGuiDTO, DuckTypes> typeCol;
    @FXML private TableColumn<DuckGuiDTO, Double> speedCol;
    @FXML private TableColumn<DuckGuiDTO, Double> rezistanceCol;

    @FXML private ComboBox<String> comboBox;

    @FXML private Button buttonNext;
    @FXML private Button buttonPrevious;

    @FXML private Label labelPage;

    private DucksService service;
    private UsersService usersService;


    public DucksController() {
        super(0, 14, 0,new DuckGUIFilter(Optional.empty()));
    }

    public void setService(DucksService service, UsersService usersService) {
        this.service = service;
        this.usersService = usersService;
        initializeTable();
        initComboBox();
        loadData();
    }

    public void initializeTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        speedCol.setCellValueFactory(new PropertyValueFactory<>("speed"));
        rezistanceCol.setCellValueFactory(new PropertyValueFactory<>("rezistance"));


        tableView.setItems(model);
    }

    public void loadData() {
        if (service == null) return;

        Pageable pageable = new Pageable(currentPage, pageSize);
        try {
            Page<Duck> duckPage = service.findAllOnPage(pageable, filter);
            int maxPage = (int) Math.ceil((double) duckPage.getTotalNumberOfElements() / pageSize) - 1;
            if (maxPage == -1) {
                maxPage = 0;
            }
            if (currentPage > maxPage) {
                currentPage = maxPage;
                duckPage = service.findAllOnPage(pageable ,filter);
            }
            totalNrOfElements = duckPage.getTotalNumberOfElements();

            labelPage.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
            buttonPrevious.setDisable(currentPage == 0);
            buttonNext.setDisable((currentPage + 1) * pageSize >= totalNrOfElements);

            model.setAll(service.getGuiDucksFromPage(duckPage));
        } catch (Exception e) {
            showAlert("Error", "Could not load data: " + e.getMessage());
        }
    }

    private void initComboBox(){
        comboBox.setItems(FXCollections.observableArrayList(service.getDuckTypes()));
        comboBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void onComboboxChange() {
        String selected = comboBox.getSelectionModel().getSelectedItem();
        if(selected.equals("All")){
            filter.setDuckType(Optional.empty());
        } else {
            filter.setDuckType(Optional.of(selected));
        }
        loadData();
    }

    @FXML
    public void handleAddDuck() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DuckAddDialog.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Duck");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(tableView.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            DuckAddDialogController controller = loader.getController();
            controller.setService(service, dialogStage);

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                loadData();
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open dialog: " + e.getMessage());
        }
    }

    @FXML
    public void handleDeleteDuck() {
        DuckGuiDTO selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            usersService.delete(selected.getId());
            model.remove(selected);
            loadData();
        } else {
            showAlert("Warning", "Please select a duck to delete.");
        }
    }


}