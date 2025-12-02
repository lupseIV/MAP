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

    public static void setUsersService(UsersService usersService) {
        GraphicUserInterface.usersService = usersService;
    }

    public static void setFriendshipService(FriendshipService friendshipService) {
        GraphicUserInterface.friendshipService = friendshipService;
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/org/ui/gui/Main.fxml")));
        Parent root = loader.load();

        MainController controller = loader.getController();
        controller.setServices(ducksService, usersService, friendshipService);

        stage.setScene(new Scene(root, 1200, 600));
        stage.setTitle("Duck Social Network");
        stage.show();
    }

    @Override
    public void run() {
        launch();
    }
}
