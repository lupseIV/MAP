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
import org.utils.enums.DuckTypes;

import java.io.IOException;
import java.util.Optional;

public class DucksController extends AbstractPagingTableViewController<DuckGuiDTO, DuckGUIFilter>{

    @FXML private TableView<DuckGuiDTO> tableView;
    @FXML private TableColumn<DuckGuiDTO, Long> idCol;
    @FXML private TableColumn<DuckGuiDTO, String> usernameCol;
    @FXML private TableColumn<DuckGuiDTO, String> emailCol;
    @FXML private TableColumn<DuckGuiDTO, Integer> nrOfFriendsCol;
    @FXML private TableColumn<DuckGuiDTO, DuckTypes> typeCol;
    @FXML private TableColumn<DuckGuiDTO, Double> speedCol;
    @FXML private TableColumn<DuckGuiDTO, Double> rezistanceCol;
    @FXML private TableColumn<DuckGuiDTO, String> flockNameCol;

    @FXML private ComboBox<String> comboBox;

    @FXML private Button buttonNext;
    @FXML private Button buttonPrevious;

    @FXML private Label labelPage;

    private DucksService service;


    public DucksController() {
        super(0, 14, 0,new DuckGUIFilter(Optional.empty()));
    }

    public void setService(DucksService service) {
        this.service = service;
        initializeTable();
        initComboBox();
        loadData();
    }

    public void initializeTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        nrOfFriendsCol.setCellValueFactory(new PropertyValueFactory<>("nrOfFriends"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        speedCol.setCellValueFactory(new PropertyValueFactory<>("speed"));
        rezistanceCol.setCellValueFactory(new PropertyValueFactory<>("rezistance"));
        flockNameCol.setCellValueFactory(new PropertyValueFactory<>("flockName"));


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
            // 1. Load the FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DuckAddDialog.fxml"));
            VBox page = loader.load();

            // 2. Create the Stage (Dialog Window)
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Duck");
            dialogStage.initModality(Modality.WINDOW_MODAL); // Blocks interaction with main window
            dialogStage.initOwner(tableView.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // 3. Inject dependencies into the Dialog Controller
            DuckAddDialogController controller = loader.getController();
            controller.setService(service, dialogStage);

            // 4. Show and Wait
            dialogStage.showAndWait();

            // 5. Refresh data if save was successful
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
            service.delete(selected.getId());
            model.remove(selected);
            loadData();
        } else {
            showAlert("Warning", "Please select a duck to delete.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}