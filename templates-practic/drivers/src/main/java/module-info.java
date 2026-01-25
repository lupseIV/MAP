module map {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens map to javafx.fxml;
    exports map;

    exports map.controller;
    opens map.controller to javafx.fxml;
    exports map.service;
    opens map.service to javafx.fxml;
    exports map.domain;
    opens map.domain to javafx.fxml;
}