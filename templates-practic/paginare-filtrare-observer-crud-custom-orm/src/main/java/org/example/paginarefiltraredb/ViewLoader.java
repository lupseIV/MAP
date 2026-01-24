package org.example.paginarefiltraredb;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.IOException;

public interface ViewLoader {
    default void loadView(String fxmlFile, ControllerConfigurator configurator, Pane contentArea){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();

            Object controller = loader.getController();
            configurator.configure(controller);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + fxmlFile);
        }
    }
}