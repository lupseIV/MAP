package org.ui.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.service.DucksService;
import org.ui.UserInterface;

import java.util.Objects;

public class GraphicUserInterface extends Application implements UserInterface {

    private static DucksService ducksService;

    public static void setDucksService(DucksService d) {
        ducksService = d;
    }

    @Override
    public void start(Stage stage) throws Exception {

        Objects.requireNonNull(ducksService, "DucksService not set. Call GraphicUserInterface.setDucksService(...) before Application.launch(...)");

        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/gui/app.fxml")));
        Parent root = loader.load();

        Controller controller = loader.getController();
        // defensive check
        Objects.requireNonNull(controller, "FXML controller is null — check fx:controller in /gui/app.fxml");
        controller.setDucksService(ducksService);

        stage.setScene(new Scene(root, 1200, 600));
        stage.setTitle("Duck Social Network");
        stage.show();
    }

    @Override
    public void run() {
        launch();
    }
}
