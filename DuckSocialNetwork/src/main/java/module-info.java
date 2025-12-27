module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;
    requires javafx.graphics;
    requires java.desktop;
    requires org.postgresql.jdbc;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports org.example;
    exports org.ui.gui;
    exports org.service;
    exports org.domain;
    exports org.repository;

    opens org.ui.gui to javafx.fxml;
    opens org.domain.dtos.guiDTOS to javafx.base;
    opens org.domain to com.fasterxml.jackson.databind;
    opens org.domain.users.relationships.notifications to com.fasterxml.jackson.databind;
    opens org.domain.users to com.fasterxml.jackson.databind;
    opens org.domain.users.relationships to com.fasterxml.jackson.databind;
    opens org.domain.events to com.fasterxml.jackson.databind;
}
