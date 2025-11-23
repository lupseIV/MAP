package org.domain.dtos.guiDTOS;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DuckGuiDTO {

    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final IntegerProperty nrOfFriends = new SimpleIntegerProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final DoubleProperty speed = new SimpleDoubleProperty();
    private final DoubleProperty rezistance = new SimpleDoubleProperty();
    private final StringProperty flockName = new SimpleStringProperty();

    public DuckGuiDTO() { }

    public DuckGuiDTO(String username, String email, int nrOfFriends, String type, Double speed, Double rezistance, String flockName) {
        this.username.set(username);
        this.email.set(email);
        this.nrOfFriends.set(nrOfFriends);
        this.type.set(type);
        if (speed != null) this.speed.set(speed);
        if (rezistance != null) this.rezistance.set(rezistance);
        this.flockName.set(flockName);
    }

    public StringProperty usernameProperty() { return username; }
    public StringProperty emailProperty() { return email; }
    public IntegerProperty nrOfFriendsProperty() { return nrOfFriends; }
    public StringProperty typeProperty() { return type; }
    public DoubleProperty speedProperty() { return speed; }
    public DoubleProperty rezistanceProperty() { return rezistance; }
    public StringProperty flockNameProperty() { return flockName; }

    public String getUsername() { return username.get(); }
    public String getEmail() { return email.get(); }
    public int getNrOfFriends() { return nrOfFriends.get(); }
    public String getType() { return type.get(); }
    public double getSpeed() { return speed.get(); }
    public double getRezistance() { return rezistance.get(); }
    public String getFlockName() { return flockName.get(); }

    public void setUsername(String username) { this.username.set(username); }
    public void setEmail(String email) { this.email.set(email); }
    public void setNrOfFriends(int nrOfFriends) { this.nrOfFriends.set(nrOfFriends); }
    public void setType(String type) { this.type.set(type); }
    public void setSpeed(double speed) { this.speed.set(speed); }
    public void setRezistance(double rezistance) { this.rezistance.set(rezistance); }
    public void setFlockName(String flockName) { this.flockName.set(flockName); }

    @Override
    public String toString() {
        return "DuckGuiDTO{" +
                "username=" + getUsername() +
                ", email=" + getEmail() +
                ", nrOfFriends=" + getNrOfFriends() +
                ", type=" + getType() +
                ", speed=" + getSpeed() +
                ", rezistance=" + getRezistance() +
                ", flockName=" + getFlockName() +
                '}';
    }
}
