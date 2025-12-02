package org.ui.gui;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.domain.dtos.filters.DuckGUIFilter;
import org.domain.dtos.guiDTOS.DuckGuiDTO;
import org.domain.dtos.guiDTOS.UserGuiDTO;
import org.domain.users.User;
import org.domain.users.duck.Duck;
import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;
import org.service.DucksService;
import org.service.FriendshipService;
import org.service.UsersService;
import org.utils.enums.DuckTypes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Controller {

    private DucksService ducksService;
    private UsersService usersService;
    private FriendshipService friendshipService;

    @FXML private TableView<DuckGuiDTO> tableView;
    @FXML private TableColumn<DuckGuiDTO, String> usernameCol;
    @FXML private TableColumn<DuckGuiDTO, String> emailCol;
    @FXML private TableColumn<DuckGuiDTO, Integer> nrOfFriendsCol;
    @FXML private TableColumn<DuckGuiDTO, DuckTypes> typeCol;
    @FXML private TableColumn<DuckGuiDTO, Double> speedCol;
    @FXML private TableColumn<DuckGuiDTO, Double> rezistanceCol;
    @FXML private TableColumn<DuckGuiDTO, String> flockNameCol;
    @FXML private ComboBox<String> comboBox;

    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button updateButton;


    @FXML private Button buttonNext;
    @FXML private Button buttonPrevious;
    @FXML private Label labelPage;

    @FXML private Label nrOfCommunitiesLabel;
    @FXML private TableView<User> mostSociableNetwork  ;
    @FXML private TableColumn<UserGuiDTO, String> usernameColCommunity;
    @FXML private TableColumn<UserGuiDTO, String> emailColCommunity;
    @FXML private TableColumn<UserGuiDTO, Integer> nrOfFriendsColCommunity;
    private final ObservableList<User> mostSociableNetworkObservableList = FXCollections.observableArrayList();

    private int pageSize = 10;
    private int currentPage = 0;
    private int totalNumberOfElements = 0;

    private final ObservableList<DuckGuiDTO> data = FXCollections.observableArrayList();
    private final ObservableList<DuckGuiDTO> masterData = FXCollections.observableArrayList();
    private DuckGUIFilter duckFilter = new DuckGUIFilter(Optional.empty());
    private final ObservableList<String> duckTypes = FXCollections.observableArrayList();

    public void setDucksService(DucksService ducksService) {
        this.ducksService = Objects.requireNonNull(ducksService);
        initTable();
    }

    public void setUsersService(UsersService usersService) {
        this.usersService = usersService;
    }

    public void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
        updateCommunitiesLabel();
        updateMostSociableCommunity();
    }

    @FXML
    private void updateCommunitiesLabel(){
        nrOfCommunitiesLabel.setText(String.valueOf(friendshipService.countFriendCommunities()));
    }

    @FXML
    private void updateMostSociableCommunity(){
        mostSociableNetworkObservableList.clear();
        mostSociableNetworkObservableList.addAll(friendshipService.findMostSociableNetwork());
    }

    @FXML
    private void initTable() {
        masterData.clear();
        masterData.addAll(ducksService.getGuiDucks());
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
        totalNumberOfElements = masterData.size();
        buttonPrevious.setDisable(currentPage == 0);
        buttonNext.setDisable((currentPage + 1) * pageSize >= totalNumberOfElements);

        data.setAll(ducksService.getGuiDucksFromPage(page));
        labelPage.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
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

        usernameColCommunity.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColCommunity.setCellValueFactory(new PropertyValueFactory<>("email"));
        nrOfFriendsColCommunity.setCellValueFactory(new PropertyValueFactory<>("nrOfFriends"));

        tableView.setItems(data);
        mostSociableNetwork.setItems(mostSociableNetworkObservableList);

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
        if(selected.equals("All")){
            duckFilter.setDuckType(Optional.empty());
        } else {
            duckFilter.setDuckType(Optional.of(selected));
        }
        initTable();
    }

    @FXML
    private void onAdd(){}

    @FXML
    private void onDelete(){}

    @FXML
    private void onUpdate(){}


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
}
