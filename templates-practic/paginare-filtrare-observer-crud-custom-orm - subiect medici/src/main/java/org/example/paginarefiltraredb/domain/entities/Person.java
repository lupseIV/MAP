package org.example.paginarefiltraredb.domain.entities;

import org.example.paginarefiltraredb.customORM.annotations.DbColumn;

public abstract class Person extends Entity<Long> {

    @DbColumn(nullable = false)
    private String name;

    @DbColumn(unique = true) // Heuristic check: "email" is usually unique
    private String email;

    @DbColumn(name = "phone_number")
    private String phone;

    // Getters/Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}