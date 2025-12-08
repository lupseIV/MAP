package org.ui.gui;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.time.LocalDate;

public class PersonFieldsController  implements FieldsController{
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField occupationField;
    @FXML private TextField emphatyField;
    @FXML private DatePicker birthDatePicker;

    public String getFirstName() {
        return firstNameField.getText();
    }

    public String getLastName() {
        return lastNameField.getText();
    }

    public String getOccupation() {
        return occupationField.getText();
    }

    public Double getEmphatyLevel() {
        return Double.parseDouble(emphatyField.getText());
    }

    public LocalDate getBirthDate() {
        return birthDatePicker.getValue();
    }

    public String isValid(){
        StringBuilder err = new StringBuilder();
        if(firstNameField.getText().isEmpty()){
            err.append("First name is empty\n");
        }
        if(lastNameField.getText().isEmpty()){
            err.append("Last name is empty\n");
        }
        if(occupationField.getText().isEmpty()){
            err.append("Occupation is empty\n");
        }
        if(emphatyField.getText().isEmpty()){
            err.append("Emphaty is empty\n");
        }
        if(birthDatePicker.getValue() == null){
            err.append("Birth date is empty\n");
        }

        try {
            Double.parseDouble(emphatyField.getText());
        } catch (NumberFormatException e) {
            err.append("Emphaty must be a number\n");
        }
        return err.toString();
    }
}