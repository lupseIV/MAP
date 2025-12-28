package org.ui.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.utils.enums.types.DuckTypes;

public class DuckFieldsController implements FieldsController{
    @FXML private ComboBox<DuckTypes> duckTypeComboBox;
    @FXML private TextField speedField;
    @FXML private TextField resistanceField;

    @FXML
    private void initialize() {
        duckTypeComboBox.setItems(FXCollections.observableArrayList(DuckTypes.values()));
    }

    public Double getSpeed() {
        return Double.parseDouble(speedField.getText());
    }

    public Double getResistance() {
        return Double.parseDouble(resistanceField.getText());
    }

    public DuckTypes getDuckType() {
        return duckTypeComboBox.getSelectionModel().getSelectedItem();
    }

    public String isValid(){
        StringBuilder err = new StringBuilder();
        if(duckTypeComboBox.getSelectionModel().getSelectedItem()==null){
            err.append("Please Select a Duck Type");
        }
        try {
            Double.parseDouble(speedField.getText());
        } catch (NumberFormatException e) {
            err.append("Please Enter a valid Speed");
        }
        try {
            Double.parseDouble(resistanceField.getText());
        } catch (NumberFormatException e) {
            err.append("Please Enter a valid resistance");
        }
        return err.toString();
    }
}