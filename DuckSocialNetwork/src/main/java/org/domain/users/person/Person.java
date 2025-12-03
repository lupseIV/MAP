package org.domain.users.person;

import org.domain.dtos.PersonData;
import org.domain.users.User;
import org.utils.enums.UserTypes;

import java.time.LocalDate;

public class Person extends User {
    private String firstName;
    private String lastName;
    private String occupation;
    private LocalDate dateOfBirth;

    private Double empathyLevel;

    public Person( String username, String password, String email, String firstName, String lastName, String occupation, LocalDate dateOfBirth, Double empathyLevel) {
        super(username, password, email,UserTypes.PERSON);
        this.firstName = firstName;
        this.lastName = lastName;
        this.occupation = occupation;
        this.dateOfBirth = dateOfBirth;
        this.empathyLevel = empathyLevel;
    }

    public Person(PersonData pd) {
        super(pd.getUsername(), pd.getPassword(), pd.getEmail(),UserTypes.PERSON);
        this.firstName = pd.getFirstName();
        this.lastName = pd.getLastName();
        this.occupation = pd.getOccupation();
        this.dateOfBirth = pd.getDateOfBirth();
        this.empathyLevel = pd.getEmpathyLevel();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Double getEmpathyLevel() {
        return empathyLevel;
    }

    public void setEmpathyLevel(Double empathyLevel) {
        this.empathyLevel = empathyLevel;
    }

    @Override
    public void login() {

    }

    @Override
    public void logout() {

    }

    @Override
    public void sendMessage() {

    }

    @Override
    public void receiveMessage() {

    }

    @Override
    public void update() {

    }

    @Override
    public String toString() {
        return super.toString()+"Person{" +
                ", empathyLevel=" + empathyLevel +
                ", dateOfBirth=" + dateOfBirth +
                ", occupation='" + occupation + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                '}';
    }
}
