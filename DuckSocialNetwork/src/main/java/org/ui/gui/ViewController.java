package org.ui.gui;

import javafx.scene.layout.Pane;

@FunctionalInterface
public interface ViewController {
    void loadView(String fxmlFile, ControllerConfigurator configurator,  Pane contentArea);
}
