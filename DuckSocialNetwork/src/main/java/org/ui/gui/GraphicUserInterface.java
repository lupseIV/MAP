package org.ui.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.service.DucksService;
import org.service.FriendshipService;
import org.service.UsersService;
import org.ui.UserInterface;

import java.util.Objects;

public class GraphicUserInterface extends Application implements UserInterface {

    private static DucksService ducksService;
    private static UsersService usersService;
    private static FriendshipService friendshipService;

    public static void setDucksService(DucksService d) {
        ducksService = d;
    }

    public static void setUsersService(UsersService u) {
        usersService = u;
    }

    public static void setFriendshipService(FriendshipService f) {
        friendshipService = f;
    }

    @Override
    public void start(Stage stage) throws Exception {

        Objects.requireNonNull(ducksService, "DucksService not set. Call GraphicUserInterface.setDucksService(...) before Application.launch(...)");
        Objects.requireNonNull(usersService, "UsersService not set. Call GraphicUserInterface.setUsersService(...) before Application.launch(...)");
        Objects.requireNonNull(friendshipService, "FriendshipService not set. Call GraphicUserInterface.setFriendshipService(...) before Application.launch(...)");

        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/gui/main.fxml")));
        Parent root = loader.load();

        MainController controller = loader.getController();
        // defensive check
        Objects.requireNonNull(controller, "FXML controller is null — check fx:controller in /gui/main.fxml");
        controller.setServices(ducksService, usersService, friendshipService);

        stage.setScene(new Scene(root, 1200, 700));
        stage.setTitle("Duck Social Network");
        stage.show();
    }

    @Override
    public void run() {
        launch();
    }
}
