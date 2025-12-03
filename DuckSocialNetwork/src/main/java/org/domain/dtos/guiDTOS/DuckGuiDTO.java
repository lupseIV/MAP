package org.domain.dtos.guiDTOS;

import javafx.beans.property.*;

public class DuckGuiDTO {

    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final DoubleProperty speed = new SimpleDoubleProperty();
    private final DoubleProperty rezistance = new SimpleDoubleProperty();


    public DuckGuiDTO(Long id,String username, String email,  String type, Double speed, Double rezistance) {
        this.id.set( id);
        this.username.set(username);
        this.email.set(email);
        this.type.set(type);
        if (speed != null) this.speed.set(speed);
        if (rezistance != null) this.rezistance.set(rezistance);
    }

    public LongProperty idProperty() { return id; }
    public StringProperty usernameProperty() { return username; }
    public StringProperty emailProperty() { return email; }
    public StringProperty typeProperty() { return type; }
    public DoubleProperty speedProperty() { return speed; }
    public DoubleProperty rezistanceProperty() { return rezistance; }

    public String getUsername() { return username.get(); }
    public String getEmail() { return email.get(); }
    public String getType() { return type.get(); }
    public double getSpeed() { return speed.get(); }
    public double getRezistance() { return rezistance.get(); }

    public long getId() {
        return id.get();
    }

    public void setUsername(String username) { this.username.set(username); }
    public void setEmail(String email) { this.email.set(email); }
    public void setType(String type) { this.type.set(type); }
    public void setSpeed(double speed) { this.speed.set(speed); }
    public void setRezistance(double rezistance) { this.rezistance.set(rezistance); }

    @Override
    public String toString() {
        return "DuckGuiDTO{" +
                "username=" + getUsername() +
                ", email=" + getEmail() +
                ", type=" + getType() +
                ", speed=" + getSpeed() +
                ", rezistance=" + getRezistance() +
                '}';
    }
}
