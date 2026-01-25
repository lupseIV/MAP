//package org.example.paginarefiltraredb.domain.dtos.implementation;
//
//import javafx.beans.property.IntegerProperty;
//import javafx.beans.property.SimpleIntegerProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.property.StringProperty;
//
//public class VipClientDto extends ClientDto {
//
//    private final IntegerProperty loyaltyPoints;
//    private final StringProperty managerName;
//
//    public VipClientDto(VipClient vip) {
//        // Pass "VIP" as the fixed type to the parent constructor
//        super(vip.getId(), vip.getName(), "VIP", String.valueOf(vip.getBudget()));
//
//        this.loyaltyPoints = new SimpleIntegerProperty(vip.getLoyaltyPoints());
//
//         Handle null manager gracefully
//        String mgrName = (vip.getPersonalManager() != null) ? vip.getPersonalManager().getName() : "Unassigned";
//        this.managerName = new SimpleStringProperty(mgrName);
//    }
//
//    public int getLoyaltyPoints() { return loyaltyPoints.get(); }
//    public IntegerProperty loyaltyPointsProperty() { return loyaltyPoints; }
//
//    public String getManagerName() { return managerName.get(); }
//    public StringProperty managerNameProperty() { return managerName; }
//}