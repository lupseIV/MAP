package org.ui.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.domain.dtos.guiDTOS.FriendshipGuiDTO;
import org.domain.dtos.guiDTOS.UserGuiDTO;
import org.domain.users.User;
import org.domain.users.relationships.Friendship;
import org.repository.util.paging.Pageable;
import org.service.FriendshipService;
import org.service.UsersService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FriendshipController {

    private FriendshipService friendshipService;
    private UsersService usersService;

    @FXML private TableView<FriendshipGuiDTO> tableView;
    @FXML private TableColumn<FriendshipGuiDTO, Long> idCol;
    @FXML private TableColumn<FriendshipGuiDTO, Long> user1IdCol;
    @FXML private TableColumn<FriendshipGuiDTO, String> user1UsernameCol;
    @FXML private TableColumn<FriendshipGuiDTO, Long> user2IdCol;
    @FXML private TableColumn<FriendshipGuiDTO, String> user2UsernameCol;

    @FXML private Button addButton;
    @FXML private Button deleteButton;

    @FXML private Button buttonNext;
    @FXML private Button buttonPrevious;
    @FXML private Label labelPage;

    private int pageSize = 5;
    private int currentPage = 0;
    private int totalNumberOfElements = 0;

    private final ObservableList<FriendshipGuiDTO> data = FXCollections.observableArrayList();
    private final ObservableList<FriendshipGuiDTO> masterData = FXCollections.observableArrayList();

    public void setServices(FriendshipService friendshipService, UsersService usersService) {
        this.friendshipService = Objects.requireNonNull(friendshipService);
        this.usersService = Objects.requireNonNull(usersService);
        Platform.runLater(this::initTable);
    }

    @FXML
    private void initTable() {
        masterData.clear();
        masterData.addAll(friendshipService.getGuiFriendships());
        List<FriendshipGuiDTO> page = friendshipService.getGuiFriendshipsPage(new Pageable(currentPage, pageSize));
        
        int maxPage = (int) Math.ceil((double) masterData.size() / pageSize) - 1;
        if (maxPage == -1) {
            maxPage = 0;
        }
        if (currentPage > maxPage) {
            currentPage = maxPage;
            page = friendshipService.getGuiFriendshipsPage(new Pageable(currentPage, pageSize));
        }
        totalNumberOfElements = masterData.size();
        buttonPrevious.setDisable(currentPage == 0);
        buttonNext.setDisable((currentPage + 1) * pageSize >= totalNumberOfElements);

        data.setAll(page);
        labelPage.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
    }

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        user1IdCol.setCellValueFactory(new PropertyValueFactory<>("user1Id"));
        user1UsernameCol.setCellValueFactory(new PropertyValueFactory<>("user1Username"));
        user2IdCol.setCellValueFactory(new PropertyValueFactory<>("user2Id"));
        user2UsernameCol.setCellValueFactory(new PropertyValueFactory<>("user2Username"));

        tableView.setItems(data);

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        idCol.setMinWidth(60);
        user1IdCol.setMinWidth(80);
        user1UsernameCol.setMinWidth(120);
        user2IdCol.setMinWidth(80);
        user2UsernameCol.setMinWidth(120);
    }

    @FXML
    private void onAdd() {
        Dialog<Friendship> dialog = new Dialog<>();
        dialog.setTitle("Add Friendship");
        dialog.setHeaderText("Enter user IDs to create friendship");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField user1IdField = new TextField();
        user1IdField.setPromptText("User 1 ID");
        TextField user2IdField = new TextField();
        user2IdField.setPromptText("User 2 ID");

        grid.add(new Label("User 1 ID:"), 0, 0);
        grid.add(user1IdField, 1, 0);
        grid.add(new Label("User 2 ID:"), 0, 1);
        grid.add(user2IdField, 1, 1);

        // Show available users for reference
        ListView<String> userListView = new ListView<>();
        ObservableList<String> userList = FXCollections.observableArrayList();
        for (UserGuiDTO user : usersService.getGuiUsers()) {
            userList.add("ID: " + user.getId() + " - " + user.getUsername());
        }
        userListView.setItems(userList);
        userListView.setPrefHeight(150);

        grid.add(new Label("Available Users:"), 0, 2);
        grid.add(userListView, 0, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    Long user1Id = Long.parseLong(user1IdField.getText().trim());
                    Long user2Id = Long.parseLong(user2IdField.getText().trim());

                    User user1 = usersService.findOne(user1Id);
                    User user2 = usersService.findOne(user2Id);

                    if (user1 == null) {
                        showError("User with ID " + user1Id + " not found.");
                        return null;
                    }
                    if (user2 == null) {
                        showError("User with ID " + user2Id + " not found.");
                        return null;
                    }

                    return new Friendship(user1, user2);
                } catch (NumberFormatException ex) {
                    showError("Invalid user ID format.");
                    return null;
                }
            }
            return null;
        });

        Optional<Friendship> result = dialog.showAndWait();
        result.ifPresent(friendship -> {
            try {
                friendshipService.save(friendship);
                initTable();
                showInfo("Friendship added successfully!");
            } catch (Exception ex) {
                showError("Error adding friendship: " + ex.getMessage());
            }
        });
    }

    @FXML
    private void onDelete() {
        FriendshipGuiDTO selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a friendship to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Friendship");
        confirm.setContentText("Are you sure you want to delete the friendship between " 
                + selected.getUser1Username() + " and " + selected.getUser2Username() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                friendshipService.delete(selected.getId());
                initTable();
                showInfo("Friendship deleted successfully!");
            } catch (Exception ex) {
                showError("Error deleting friendship: " + ex.getMessage());
            }
        }
    }

    public void onNextPage(ActionEvent actionEvent) {
        if ((currentPage + 1) * pageSize < totalNumberOfElements) {
            currentPage++;
            initTable();
        }
    }

    public void onPreviousPage(ActionEvent actionEvent) {
        if (currentPage > 0) {
            currentPage--;
            initTable();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
