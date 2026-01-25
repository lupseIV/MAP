package org.example.paginarefiltraredb.domain.dtos.implementation;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.paginarefiltraredb.domain.entities.Order;

import java.time.LocalDateTime;

public class OrderDTO {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty pickUpAddress = new SimpleStringProperty();
    private StringProperty destinatioAddress = new SimpleStringProperty();

    public OrderDTO(Integer id, String pickUpAddress, String destinatioAddress) {
        this.id.setValue(id);
        this.pickUpAddress.setValue(pickUpAddress);
        this.destinatioAddress.setValue(destinatioAddress);
    }

    public OrderDTO(Order order) {
        this(order.getId(), order.getPickUpAddress(), order.getDestinationAddress());
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getPickUpAddress() {
        return pickUpAddress.get();
    }

    public StringProperty pickUpAddressProperty() {
        return pickUpAddress;
    }

    public void setPickUpAddress(String pickUpAddress) {
        this.pickUpAddress.set(pickUpAddress);
    }

    public String getDestinatioAddress() {
        return destinatioAddress.get();
    }

    public StringProperty destinatioAddressProperty() {
        return destinatioAddress;
    }

    public void setDestinatioAddress(String destinatioAddress) {
        this.destinatioAddress.set(destinatioAddress);
    }
}
