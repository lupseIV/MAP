package org.example.paginarefiltraredb.domain.entities;

import org.example.paginarefiltraredb.customORM.annotations.DbColumn;
import org.example.paginarefiltraredb.customORM.annotations.DbManyToOne;
import org.example.paginarefiltraredb.customORM.annotations.DbTable;

import java.time.LocalDate;

@DbTable(name = "vip_clients")
public class VipClient extends Client {

    @DbColumn(name = "loyalty_points")
    private int loyaltyPoints;

    @DbColumn(name = "assigned_manager_id")
    @DbManyToOne // Foreign Key to a Manager
    private Manager personalManager;

    public VipClient(String name, String type, Double budget, LocalDate registrationDate) {
        super(name, type, budget, registrationDate);
    }

    public VipClient() {
    }

    // Getters/Setters
    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }
    public Manager getPersonalManager() { return personalManager; }
    public void setPersonalManager(Manager personalManager) { this.personalManager = personalManager; }
}