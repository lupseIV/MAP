package org.domain.dtos.guiDTOS;

import javafx.beans.property.*;

import java.time.LocalDate;

public class PersonGuiDTO {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty firstName = new SimpleStringProperty();
    private final StringProperty lastName = new SimpleStringProperty();
    private final StringProperty occupation =  new SimpleStringProperty();
    private final StringProperty dateOfBirth = new SimpleStringProperty();

    private final DoubleProperty empathyLevel =  new SimpleDoubleProperty();

    public PersonGuiDTO(Long id, String username, String email,
                        String firstName, String lastName, String occupation,
                        String dateOfBirth, Double empathyLevel) {
        this.id.set(id);
        this.username.set(username);
        this.email.set(email);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.occupation.set(occupation);
        this.dateOfBirth.set(dateOfBirth);
        this.empathyLevel.set(empathyLevel);

    }

    public LongProperty getIdProperty() {
        return id;
    }
    public StringProperty getUsernameProperty() { return username; }
    public StringProperty getEmailProperty() { return email; }
    public StringProperty getFirstNameProperty() { return firstName; }
    public StringProperty getLastNameProperty() { return lastName; }
    public StringProperty getOccupationProperty() { return occupation; }
    public StringProperty getDateOfBirthProperty() { return dateOfBirth; }
    public DoubleProperty getEmpathyLevelProperty() { return empathyLevel; }

    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public String getOccupation() {
        return occupation.get();
    }

    public StringProperty occupationProperty() {
        return occupation;
    }

    public String getDateOfBirth() {
        return dateOfBirth.get();
    }

    public StringProperty dateOfBirthProperty() {
        return dateOfBirth;
    }

    public double getEmpathyLevel() {
        return empathyLevel.get();
    }

    public DoubleProperty empathyLevelProperty() {
        return empathyLevel;
    }
}
