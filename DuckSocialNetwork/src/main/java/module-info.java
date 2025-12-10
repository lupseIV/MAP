module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;
    requires javafx.graphics;
    requires java.desktop;
    requires org.postgresql.jdbc;

    // Export packages to make them visible to other modules
    exports org.example;
    exports org.ui.gui;
    exports org.domain.dtos.guiDTOS;
    exports org.domain.events;
    exports org.service;
    exports org.domain.users.relationships.messages;

    // Open packages to allow JavaFX to access them via reflection
    opens org.ui.gui to javafx.fxml;
    opens org.domain.dtos.guiDTOS to javafx.base;
}
