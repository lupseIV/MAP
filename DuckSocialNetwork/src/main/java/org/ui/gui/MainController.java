package org.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import java.io.IOException;
import java.net.URL;

import org.service.DucksService;
import org.service.FriendshipService;
import org.service.UsersService;

public class MainController {

    @FXML private StackPane mainContent;

    private DucksController ducksController;
    private FriendshipsController friendshipsController;
    private UsersController usersController;

    public void setServices(DucksService ducksService, UsersService usersService, FriendshipService friendshipService) throws IOException {
        URL ducksUrl = getClass().getResource("/org/ui/gui/ducks.fxml");
        URL friendshipsUrl = getClass().getResource("/org/ui/gui/friendship.fxml");
        URL usersUrl = getClass().getResource("/org/ui/gui/users.fxml");

        if (ducksUrl == null) throw new IOException("FXML not found: /org/ui/gui/ducks.fxml. Ensure it is under src/main/resources/org/ui/gui/.");
        if (friendshipsUrl == null) throw new IOException("FXML not found: /org/ui/gui/friendship.fxml. Ensure it is under src/main/resources/org/ui/gui/.");
        if (usersUrl == null) throw new IOException("FXML not found: /org/ui/gui/users.fxml. Ensure it is under src/main/resources/org/ui/gui/.");

        try {
            FXMLLoader ducksLoader = new FXMLLoader(ducksUrl);
            Pane ducksUI = ducksLoader.load();
            ducksController = ducksLoader.getController();
            ducksController.setDucksService(ducksService);

            FXMLLoader friendshipsLoader = new FXMLLoader(friendshipsUrl);
            Pane friendshipsUI = friendshipsLoader.load();
            friendshipsController = friendshipsLoader.getController();
            friendshipsController.setFriendshipService(friendshipService);

            FXMLLoader usersLoader = new FXMLLoader(usersUrl);
            Pane usersUI = usersLoader.load();
            usersController = usersLoader.getController();
            usersController.setUsersService(usersService);

            mainContent.getChildren().setAll(ducksUI);
        } catch (IOException | RuntimeException e) {
            throw new IOException("Failed to load FXML: " + e.getMessage(), e);
        }
    }

    @FXML
    private void showDucks() {
        mainContent.getChildren().setAll(ducksController.getRootPane());
    }

    @FXML
    private void showFriendships() {
        mainContent.getChildren().setAll(friendshipsController.getRootPane());
    }

    @FXML
    private void showUsers() {
        mainContent.getChildren().setAll(usersController.getRootPane());
    }
}
