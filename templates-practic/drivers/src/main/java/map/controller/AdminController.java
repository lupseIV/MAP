package map.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import map.service.Service;

public class AdminController {
    public TextField adresaPlecare;
    public TextField numeClient;
    public TextField adresaDestinatie;
    Service service;

    public void setService(Service service){
        this.service = service;
    }

    public void handleAddOrder(ActionEvent actionEvent) {
        try{
            service.AddOrder(adresaPlecare.getText(), adresaDestinatie.getText(), numeClient.getText());
        }catch (RuntimeException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }
}
