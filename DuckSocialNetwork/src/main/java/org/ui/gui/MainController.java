package org.ui.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.service.DucksService;
import org.service.FriendshipService;
import org.service.UsersService;

import java.io.IOException;
import java.util.Objects;

public class MainController {

    private DucksService ducksService;
    private UsersService usersService;
    private FriendshipService friendshipService;

    @FXML private TabPane tabPane;

    public void setServices(DucksService ducksService, UsersService usersService, FriendshipService friendshipService) {
        this.ducksService = Objects.requireNonNull(ducksService);
        this.usersService = Objects.requireNonNull(usersService);
        this.friendshipService = Objects.requireNonNull(friendshipService);
        Platform.runLater(this::initTabs);
    }

    @FXML
    private void initialize() {
        // Tab pane will be initialized after services are set
    }

    private void initTabs() {
        try {
            // Load Ducks tab
            FXMLLoader ducksLoader = new FXMLLoader(getClass().getResource("/gui/app.fxml"));
            Parent ducksContent = ducksLoader.load();
            Controller ducksController = ducksLoader.getController();
            ducksController.setDucksService(ducksService);
            Tab ducksTab = new Tab("Ducks", ducksContent);
            ducksTab.setClosable(false);

            // Load Users tab
            FXMLLoader usersLoader = new FXMLLoader(getClass().getResource("/gui/users.fxml"));
            Parent usersContent = usersLoader.load();
            UserController userController = usersLoader.getController();
            userController.setUsersService(usersService);
            Tab usersTab = new Tab("Users", usersContent);
            usersTab.setClosable(false);

            // Load Friendships tab
            FXMLLoader friendshipsLoader = new FXMLLoader(getClass().getResource("/gui/friendships.fxml"));
            Parent friendshipsContent = friendshipsLoader.load();
            FriendshipController friendshipController = friendshipsLoader.getController();
            friendshipController.setServices(friendshipService, usersService);
            Tab friendshipsTab = new Tab("Friendships", friendshipsContent);
            friendshipsTab.setClosable(false);

            tabPane.getTabs().addAll(ducksTab, usersTab, friendshipsTab);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML resources for tabs", e);
        }
    }
}
