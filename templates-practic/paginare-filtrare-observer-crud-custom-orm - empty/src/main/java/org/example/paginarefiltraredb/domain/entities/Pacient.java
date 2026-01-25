package org.example.paginarefiltraredb.domain.entities;

import org.example.paginarefiltraredb.customORM.annotations.DbColumn;
import org.example.paginarefiltraredb.customORM.annotations.DbTable;

@DbTable(name = "pacients")
public class Pacient extends Entity<Long> {

    @DbColumn(name = "name", nullable = false)
    private String name;

    @DbColumn(name = "cnp", nullable = false, unique = true)
    private String cnp;

    public Pacient(Long id, String name, String cnp) {
        this.name = name;
        this.cnp = cnp;
        this.setId(id);
    }

    public Pacient(String cnp, String name) {
        this.cnp = cnp;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }
}
