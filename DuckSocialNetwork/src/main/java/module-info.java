module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;
    requires javafx.graphics;

    // Export packages to make them visible to other modules
    exports org.example;
    exports org.ui.gui;
    exports org.domain.dtos.guiDTOS;

    // Open packages to allow JavaFX to access them via reflection
    opens org.ui.gui to javafx.fxml;
    opens org.domain.dtos.guiDTOS to javafx.base;
}
