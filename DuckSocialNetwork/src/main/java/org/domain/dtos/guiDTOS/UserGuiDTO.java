package org.domain.dtos.guiDTOS;

import javafx.beans.property.*;

public class UserGuiDTO {
    private  StringProperty username = new SimpleStringProperty();
    private  StringProperty email = new SimpleStringProperty();
    private  IntegerProperty nrOfFriends = new SimpleIntegerProperty();
    public UserGuiDTO(String username, String email, int nrOfFriends) {
        this.username.set(username);
        this.email.set(email);
        this.nrOfFriends.set(nrOfFriends);
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

    public int getNrOfFriends() {
        return nrOfFriends.get();
    }

    public IntegerProperty nrOfFriendsProperty() {
        return nrOfFriends;
    }

    public void setNrOfFriends(int nrOfFriends) {
        this.nrOfFriends.set(nrOfFriends);
    }

    @Override
    public String toString() {
        return "UserGuiDTO{" +
                "username=" + username +
                ", email=" + email +
                ", nrOfFriends=" + nrOfFriends +
                '}';
    }
}
