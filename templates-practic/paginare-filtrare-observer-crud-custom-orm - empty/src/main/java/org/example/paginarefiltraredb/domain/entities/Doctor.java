package org.example.paginarefiltraredb.domain.entities;

import org.example.paginarefiltraredb.customORM.annotations.DbColumn;
import org.example.paginarefiltraredb.customORM.annotations.DbTable;

@DbTable(name = "doctors")
public class Doctor extends Entity<Long> {

    @DbColumn(name = "name", nullable = false)
    private String name;

    @DbColumn(name = "specialty", nullable = false)
    private String specialty;

    public Doctor(Long id, String name, String specialty) {
        this.name = name;
        this.specialty = specialty;
        super.setId(id);
    }

    public Doctor(String name, String specialty) {
        this.name = name;
        this.specialty = specialty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}
