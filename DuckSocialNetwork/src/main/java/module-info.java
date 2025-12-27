module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;
    requires javafx.graphics;
    requires java.desktop;
    requires org.postgresql.jdbc;
    requires org.example;

    exports org.example;
    exports org.ui.gui;
    exports org.service;
    exports org.domain;
    exports org.repository;

    opens org.ui.gui to javafx.fxml;
    opens org.domain.dtos.guiDTOS to javafx.base;
}
