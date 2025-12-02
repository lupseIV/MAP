package org.domain.dtos.guiDTOS;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserGuiDTO {

    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final IntegerProperty nrOfFriends = new SimpleIntegerProperty();
    private final StringProperty userType = new SimpleStringProperty();

    public UserGuiDTO() { }

    public UserGuiDTO(Long id, String username, String email, int nrOfFriends, String userType) {
        this.id.set(id);
        this.username.set(username);
        this.email.set(email);
        this.nrOfFriends.set(nrOfFriends);
        this.userType.set(userType);
    }

    public LongProperty idProperty() { return id; }
    public StringProperty usernameProperty() { return username; }
    public StringProperty emailProperty() { return email; }
    public IntegerProperty nrOfFriendsProperty() { return nrOfFriends; }
    public StringProperty userTypeProperty() { return userType; }

    public Long getId() { return id.get(); }
    public String getUsername() { return username.get(); }
    public String getEmail() { return email.get(); }
    public int getNrOfFriends() { return nrOfFriends.get(); }
    public String getUserType() { return userType.get(); }

    public void setId(Long id) { this.id.set(id); }
    public void setUsername(String username) { this.username.set(username); }
    public void setEmail(String email) { this.email.set(email); }
    public void setNrOfFriends(int nrOfFriends) { this.nrOfFriends.set(nrOfFriends); }
    public void setUserType(String userType) { this.userType.set(userType); }

    @Override
    public String toString() {
        return "UserGuiDTO{" +
                "id=" + getId() +
                ", username=" + getUsername() +
                ", email=" + getEmail() +
                ", nrOfFriends=" + getNrOfFriends() +
                ", userType=" + getUserType() +
                '}';
    }
}
