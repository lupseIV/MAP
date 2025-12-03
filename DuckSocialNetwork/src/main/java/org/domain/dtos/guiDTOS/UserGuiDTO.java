package org.domain.dtos.guiDTOS;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserGuiDTO {

    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty type  = new SimpleStringProperty();

    public UserGuiDTO(Long id, String username, String email, String type) {
        this.id.set(id);
        this.username.set(username);
        this.email.set(email);
        this.type.set(type);
    }

    public LongProperty getIdProperty() { return id; }
    public StringProperty getUsernameProperty() { return username; }
    public StringProperty getEmailProperty() { return email; }
    public StringProperty getTypeProperty() { return type; }

    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }
}
