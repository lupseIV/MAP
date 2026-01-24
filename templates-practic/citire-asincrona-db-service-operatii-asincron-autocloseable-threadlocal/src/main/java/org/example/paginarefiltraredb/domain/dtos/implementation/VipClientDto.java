package org.example.paginarefiltraredb.domain.dtos.implementation;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.paginarefiltraredb.customORM.annotations.DbColumn;
import org.example.paginarefiltraredb.customORM.annotations.DbManyToOne;
import org.example.paginarefiltraredb.domain.entities.Client;
import org.example.paginarefiltraredb.domain.entities.Manager;
import org.example.paginarefiltraredb.domain.entities.VipClient;

public class VipClientDto extends ClientDto {

    private  IntegerProperty loyaltyPoints;
    private final StringProperty managerName;

    public VipClientDto(VipClient vip) {
        // Pass "VIP" as the fixed type to the parent constructor
        super(vip.getId(), vip.getName(), "VIP", String.valueOf(vip.getBudget()));

        this.loyaltyPoints = new SimpleIntegerProperty(vip.getLoyaltyPoints());

        // Handle null manager gracefully
        String mgrName = (vip.getPersonalManager() != null) ? vip.getPersonalManager().getName() : "Unassigned";
        this.managerName = new SimpleStringProperty(mgrName);
    }

    public int getLoyaltyPoints() { return loyaltyPoints.get(); }
    public IntegerProperty loyaltyPointsProperty() { return loyaltyPoints; }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints.set(loyaltyPoints);
    }

    public String getManagerName() { return managerName.get(); }
    public StringProperty managerNameProperty() { return managerName; }
}