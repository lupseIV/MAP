package org.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.domain.dtos.filters.DuckGUIFilter;
import org.domain.dtos.guiDTOS.DuckGuiDTO;
import org.domain.users.duck.Duck;
import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;
import org.service.DucksService;
import org.utils.enums.DuckTypes;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DucksController {

    @FXML private VBox rootPane;
    @FXML private TableView<DuckGuiDTO> tableView;
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

    private DucksService ducksService;

    private final int pageSize = 10;
    private int currentPage = 0;
    private int totalNumberOfElements = 0;

    private final ObservableList<DuckGuiDTO> data = FXCollections.observableArrayList();
    private final DuckGUIFilter duckFilter = new DuckGUIFilter(Optional.empty());

    public VBox getRootPane() {
        return rootPane;
    }

    public void setDucksService(DucksService service) {
        this.ducksService = Objects.requireNonNull(service);
        initTable();
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

        initComboBox();
    }

    private void initComboBox() {
        comboBox.getItems().clear();
        comboBox.getItems().add("All");
        for (DuckTypes dt : DuckTypes.values()) {
            comboBox.getItems().add(dt.name());
        }
        comboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void filterDucks(javafx.event.ActionEvent actionEvent) {
        String selected = comboBox.getSelectionModel().getSelectedItem();
        if(selected.equals("All")){
            duckFilter.setDuckType(Optional.empty());
        } else {
            duckFilter.setDuckType(Optional.of(selected));
        }
        initTable();
    }

    @FXML
    public void onNextPage(ActionEvent actionEvent) {
        if ((currentPage + 1) * pageSize < totalNumberOfElements) {
            currentPage++;
            initTable();
        }
    }

    @FXML
    public void onPreviousPage(ActionEvent actionEvent) {
        if (currentPage > 0) {
            currentPage--;
            initTable();
        }
    }

    private void initTable() {
        Page<Duck> page = ducksService.findAllOnPage(new Pageable(currentPage, pageSize), duckFilter);
        // after delete, the number of pages might decrease
        int maxPage = (int) Math.ceil((double) page.getTotalNumberOfElements() / pageSize) - 1;
        if (maxPage == -1) {
            maxPage = 0;
        }
        if (currentPage > maxPage) {
            currentPage = maxPage;
            page = ducksService.findAllOnPage(new Pageable(currentPage, pageSize) ,duckFilter);
        }
        totalNumberOfElements = page.getTotalNumberOfElements();
        buttonPrevious.setDisable(currentPage == 0);
        buttonNext.setDisable((currentPage + 1) * pageSize >= totalNumberOfElements);

        data.setAll(ducksService.getGuiDucksFromPage(page));
        labelPage.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
    }
}
