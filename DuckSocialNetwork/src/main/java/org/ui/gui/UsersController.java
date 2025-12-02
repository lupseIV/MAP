package org.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.domain.dtos.guiDTOS.UserGuiDTO;
import org.domain.users.User;
import org.service.UsersService;

import java.util.Objects;

public class UsersController {

    @FXML VBox rootPane;
    @FXML private TableView<UserGuiDTO> usersTable;
    @FXML private TableColumn<UserGuiDTO, String> usernameCol;
    @FXML private TableColumn<UserGuiDTO, String> emailCol;

    private UsersService usersService;
    private final ObservableList<UserGuiDTO> data = FXCollections.observableArrayList();

    public void setUsersService(UsersService service) {
        this.usersService = Objects.requireNonNull(service);
        loadUsers();
    }

    public VBox getRootPane() {
        return rootPane;
    }

    @FXML
    private void initialize() {
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        usersTable.setItems(data);
    }

    private void loadUsers() {
        data.setAll( usersService.getGuiUsers());
    }
}
