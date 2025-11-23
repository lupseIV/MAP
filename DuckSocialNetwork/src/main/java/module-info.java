// java
module ducksocial {
    requires java.sql;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // open the GUI package(s) used by FXMLLoader
    opens org.ui.gui to javafx.fxml;

    // open DTOs if they use JavaFX properties/reflection
    opens org.domain.dtos.guiDTOS to javafx.base;

    // export only packages other modules need
    exports org.example;
}
