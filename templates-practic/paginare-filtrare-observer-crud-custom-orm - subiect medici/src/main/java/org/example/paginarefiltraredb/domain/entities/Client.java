package org.example.paginarefiltraredb.domain.entities;

import org.example.paginarefiltraredb.customORM.annotations.DbColumn;
import org.example.paginarefiltraredb.customORM.annotations.DbTable;

import java.time.LocalDate;

@DbTable(name = "clients")
public class Client extends Entity<Integer> {

    @DbColumn(name = "full_name", nullable = false)
    private String name;

    @DbColumn(name = "client_type") // e.g., "Corporate", "Individual"
    private String type;

    @DbColumn(nullable = false)
    private Double budget;

    @DbColumn(name = "resgitration",  nullable = false)
    private LocalDate registrationDate;

    // Default constructor (Required for Reflection/JDBC)
    public Client() {}

    public Client(String name, String type, Double budget, LocalDate registrationDate) {
        this.name = name;
        this.type = type;
        this.budget = budget;
        this.registrationDate = registrationDate;
    }

    // Getters and Setters (Crucial for JavaFX PropertyValueFactory)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}
